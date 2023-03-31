package org.stella.typecheck

import com.sun.jdi.InvalidTypeException
import org.syntax.stella.Absyn.*
import java.lang.Exception
import kotlin.jvm.Throws

/**
 * Base class for type checking logic process.
 * Works with given [Program], checks for all type inconsistencies without manual execution of the program.
 *
 * The type check is failed if any [Exception] arises.
 */
class ProgramTypeChecker {
    /**
     * Entry point for starting the type check of the program.
     *
     * @param program: AST of the program to check
     *
     * @throws [Exception] if program is not parsable
     * @throws [InvalidTypeException] if function has incorrect expected and actual return type
     * @throws [NoSuchMethodException] if main method is not present in the program
     */
    fun startTypeCheck(program: Program) {
        val startingContext = FunctionContext()

        // start by checking if program has appropriate type and AST
        if (program is AProgram) {

            // run through all the program functions and type check them
            program.listdecl_.forEach { decl ->
                if (decl is DeclFun) {

                    // init function parameters value
                    val (paramName, paramType) = getParamType(decl.listparamdecl_.first)

                    // init function data
                    val funcName = decl.stellaident_
                    val returnType = getReturnType(decl.returntype_)

                    // add the function to the current context, to mark them for further use
                    startingContext.funcNameToFuncType[funcName] = Types.Fun(paramType, returnType)
                    startingContext.currentFuncParams[paramName] = paramType

                    // get actual return type of the function
                    val actualReturnType = parseExpr(decl.expr_, startingContext)

                    if (actualReturnType != returnType) {
                        throwTypeError(
                            lineNumber = decl.line_num,
                            expectedType = returnType,
                            actualType = actualReturnType,
                            expr = funcName,
                        )
                    }
                }
            }

            if (!startingContext.funcNameToFuncType.contains("main")) {
                throw NoSuchMethodException("Main method is missing!")
            }
        } else {
            throw Exception("The invalid program AST was given!")
        }
    }

    /**
     * Recursive algorithm to check for any inconsistencies in the expression.
     *
     * @param expr expression to check
     * @param context current context of variables and function available to use
     *
     * @return return type of the expression
     *
     * @throws Exception if an inconsistency was met
     */
    private fun parseExpr(expr: Expr, context: FunctionContext): Types {

        // work with the exact type of the expression
        return when (expr) {
            is Abstraction -> {
                val (paramName, paramType) = getParamType(expr.listparamdecl_.first)

                // append the context with the new given parameter
                val newContext = context.also {
                    it.currentFuncParams[paramName] = paramType
                }

                // create new function, as Abstraction is
                Types.Fun(paramType, parseExpr(expr.expr_, newContext))
            }
            is Application -> {
                val funcType = parseExpr(expr.expr_, context)
                val paramType = parseExpr(expr.listexpr_.first, context)

                val funcAsTypeFun = funcType as? Types.Fun

                // check if argument is applied to function, not to any other construction
                if (funcAsTypeFun == null) {
                    throwTypeError(
                        lineNumber = expr.line_num,
                        expectedType = Types.Fun(paramType, Types.Undefined),
                        actualType = Types.Undefined,
                        expr = "Application",
                    )
                } else {

                    // check if valid type of parameter is passed to function
                    if (funcType.inputType != paramType) {
                        throwTypeError(
                            lineNumber = expr.line_num,
                            expectedType = funcType.inputType,
                            actualType = paramType,
                            expr = "Application",
                        )
                    }
                }

                funcAsTypeFun!!.outputType
            }
            is ConstFalse -> {
                Types.Bool
            }
            is ConstTrue -> {
                Types.Bool
            }
            is ConstInt -> {
                Types.Nat
            }
            is If -> {
                val ifExprType = parseExpr(expr.expr_1, context)
                val onSuccessExprType = parseExpr(expr.expr_2, context)
                val onFailExprType = parseExpr(expr.expr_3, context)

                // check if the condition expression is of the Bool type
                if (ifExprType != Types.Bool) {
                    throwTypeError(
                        lineNumber = expr.line_num,
                        expectedType = Types.Bool,
                        actualType = ifExprType,
                        expr = "If"
                    )
                }

                // check if both success and fail branches return the value of the same type
                if (onSuccessExprType != onFailExprType) {
                    throwTypeError(
                        lineNumber = expr.line_num,
                        expectedType = onSuccessExprType,
                        actualType = onFailExprType,
                        expr = "If ??? then ? else ?"
                    )
                }

                onSuccessExprType
            }

            is NatRec -> {
                val countExprType = parseExpr(expr.expr_1, context)
                val baseValueType = parseExpr(expr.expr_2, context)
                val mapFuncType = parseExpr(expr.expr_3, context)

                // check if count value has the correct type of TypeNat
                if (countExprType !is Types.Nat) {
                    throwTypeError(
                        lineNumber = expr.line_num,
                        expectedType = Types.Nat,
                        actualType = countExprType,
                        expr = "Nat::rec",
                    )
                }

                val mapFuncAsTypeFun = mapFuncType as? Types.Fun

                // check if map function is actually the function
                if (mapFuncAsTypeFun != null) {
                    if (mapFuncType.inputType != countExprType) {
                        throwTypeError(
                            lineNumber = expr.line_num,
                            expectedType = countExprType,
                            actualType = mapFuncType.inputType,
                            expr = "Map function for Nat::rec",
                        )
                    }

                    val subMapFuncType = mapFuncType.outputType
                    val subMapFuncAsTypeFun = mapFuncType.outputType as? Types.Fun

                    // check if sub-map function is actually the function
                    if (subMapFuncAsTypeFun != null) {

                        // check if the input type for sub-map function is the same as type of base value
                        if (subMapFuncAsTypeFun.inputType != baseValueType) {
                            throwTypeError(
                                lineNumber = expr.line_num,
                                expectedType = baseValueType,
                                actualType = subMapFuncAsTypeFun.inputType,
                                expr = "Sub function for mapper in Nat::rec",
                            )
                        }

                        // check if the output type for sub-map function is the same as type of base value
                        if (subMapFuncAsTypeFun.outputType != baseValueType) {
                            throwTypeError(
                                lineNumber = expr.line_num,
                                expectedType = baseValueType,
                                actualType = subMapFuncAsTypeFun.outputType,
                                expr = "Sub function for mapper in Nat::rec",
                            )
                        }
                    } else {
                        throwTypeError(
                            lineNumber = expr.line_num,
                            expectedType = Types.Fun(baseValueType, baseValueType),
                            actualType = subMapFuncType,
                            expr = "Sub function for mapper in Nat::rec",
                        )
                    }
                } else {
                    throwTypeError(
                        lineNumber = expr.line_num,
                        expectedType = Types.Fun(
                            inputType = countExprType,
                            outputType = Types.Fun(baseValueType, baseValueType)
                        ),
                        actualType = mapFuncType,
                        expr = "Map function for Nat::rec",
                    )
                }

                baseValueType
            }

            is Succ -> {
                val subExprType = parseExpr(expr.expr_, context)

                // check if valid TypeNat value is passed to succ expression
                if (subExprType != Types.Nat) {
                    throwTypeError(
                        lineNumber = expr.line_num,
                        expectedType = Types.Nat,
                        actualType = subExprType,
                        expr = "succ function",
                    )
                }

                subExprType
            }

            is Var -> {
                var variableType: Types = Types.Undefined

                // check if the given var is either a function or variable
                variableType = if (context.currentFuncParams.contains(expr.stellaident_)) {
                    context.currentFuncParams[expr.stellaident_]!!
                } else if (context.funcNameToFuncType.contains(expr.stellaident_)) {
                    context.funcNameToFuncType[expr.stellaident_]!!
                } else {
                    throw Exception("${expr.stellaident_} is not defined!")
                }

                variableType
            }

            else -> {
                throw Exception("Unsupported expression type!")
            }
        }
    }

    /**
     * Parse the information about the given parameter.
     *
     * @param paramDecl to be parsed
     *
     * @return pair of values, which are name of the parameter and its type
     */
    private fun getParamType(paramDecl: ParamDecl): Pair<String, Types> {
        if (paramDecl !is AParamDecl) {
            throw InvalidTypeException("Invalid type of parameter!")
        }

        val paramName = paramDecl.stellaident_
        val paramType = parseType(paramDecl.type_)

        return Pair(paramName, paramType)
    }

    /**
     * Parse the information about the return type of the function.
     *
     * @param returnType the actual return type of the function
     *
     * @return type of the return expression
     * @throws InvalidTypeException if the return type is not of the correct type
     */
    private fun getReturnType(returnType: ReturnType): Types {
        return when (returnType) {
            is SomeReturnType -> {
                parseType(returnType.type_)
            }
            is NoReturnType -> {
                Types.Unit
            }
            else -> {
                throw InvalidTypeException("Invalid type of return value!")
            }
        }
    }

    /**
     * Parses the full type in the format of [Types].
     *
     * @param type actual given type
     *
     * @return the formatted type as [Types]
     */
    private fun parseType(type: Type): Types {
        return if (type is TypeFun) {
            val paramType = parseType(type.listtype_.first)
            val returnType = parseType(type.type_)

            Types.Fun(paramType, returnType)
        } else {
            Types.getBaseType(type)
        }
    }

    /**
     * Throws the formatted type exception.
     *
     * @param lineNumber line number where the errored has occurred
     * @param expectedType expected type
     * @param actualType actual type received
     * @param expr expression description where the error occurred
     */
    @Throws(InvalidTypeException::class)
    private fun throwTypeError(lineNumber: Int, expectedType: Types, actualType: Types, expr: String) {
        throw InvalidTypeException(
            "Error at line $lineNumber: \n" +
                    "Expected type $expectedType but got $actualType\n" +
                    "For $expr"
        )
    }

    /**
     * Context class for program.
     * It stores the description of the function by its name.
     * It stores the type of the variable by its name.
     */
    private class FunctionContext {
        val funcNameToFuncType = mutableMapOf<String, Types.Fun>()
        val currentFuncParams = mutableMapOf<String, Types>()
    }
}