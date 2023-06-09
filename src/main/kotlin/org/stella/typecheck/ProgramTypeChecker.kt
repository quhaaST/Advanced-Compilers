package org.stella.typecheck

import com.sun.jdi.InvalidTypeException
import org.syntax.stella.Absyn.*

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
                    startingContext.funcNameToFuncType[funcName] = Types.Fun(mutableListOf(), paramType, returnType)
                    startingContext.currentFuncParams[paramName] = paramType

                    // get actual return type of the function
                    val actualReturnType = parseExpr(decl.expr_, startingContext)

                    if (returnType != actualReturnType) {
                        throwTypeError(
                            lineNumber = decl.line_num,
                            expectedType = returnType,
                            actualType = actualReturnType,
                            expr = funcName,
                        )
                    }
                } else if (decl is DeclFunGeneric) {
                    // init function parameters value
                    val (paramName, paramType) = getParamType(decl.listparamdecl_.first)

                    // init function data
                    // create a list of required generic parameters
                    val genericNames = mutableListOf<String>()
                    for (genericVar in decl.liststellaident_) {
                        genericNames.add(genericVar)
                    }

                    val funcName = decl.stellaident_
                    val returnType = getReturnType(decl.returntype_)

                    // add the function to the current context, to mark them for further use
                    startingContext.funcNameToFuncType[funcName] = Types.Fun(genericNames, paramType, returnType)
                    startingContext.currentFuncParams[paramName] = paramType

                    // get actual return type of the function
                    val actualReturnType = parseExpr(decl.expr_, startingContext)

                    if (returnType != actualReturnType) {
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
     * @throws ArrayIndexOutOfBoundsException if trying to access parameter at invalid position
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
                Types.Fun(mutableListOf(), paramType, parseExpr(expr.expr_, newContext))
            }

            is Application -> {
                val funcType = parseExpr(expr.expr_, context)
                val paramType = parseExpr(expr.listexpr_.first, context)

                val funcAsTypeFun = funcType as? Types.Fun

                // check if argument is applied to function, not to any other construction
                if (funcAsTypeFun == null) {
                    throwTypeError(
                        lineNumber = expr.line_num,
                        expectedType = Types.Fun(mutableListOf(), paramType, Types.Undefined),
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

            is ConstUnit -> {
                Types.Unit
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
                            expectedType = Types.Fun(mutableListOf(), baseValueType, baseValueType),
                            actualType = subMapFuncType,
                            expr = "Sub function for mapper in Nat::rec",
                        )
                    }
                } else {
                    throwTypeError(
                        lineNumber = expr.line_num,
                        expectedType = Types.Fun(
                            inputType = countExprType,
                            outputType = Types.Fun(mutableListOf(), baseValueType, baseValueType)
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
                val variableType = if (expr.stellaident_ == "panic!") {
                    Types.Panic
                } else {
                    // check if the given var is either a function or variable
                    if (context.currentFuncParams.contains(expr.stellaident_)) {
                        context.currentFuncParams[expr.stellaident_]!!
                    } else if (context.funcNameToFuncType.contains(expr.stellaident_)) {
                        context.funcNameToFuncType[expr.stellaident_]!!
                    } else {
                        throw Exception("${expr.stellaident_} is not defined!")
                    }
                }

                variableType
            }

            is Tuple -> {
                val paramsList = mutableListOf<Types>()

                for (param in expr.listexpr_) {
                    paramsList.add(parseExpr(param, context))
                }

                Types.Tuple(paramsList)
            }

            is DotTuple -> {
                val tuple = parseExpr(expr.expr_, context)
                val position = expr.integer_

                val tupleAsTypeTuple = tuple as? Types.Tuple
                if (tupleAsTypeTuple != null) {
                    if ((position - 1) !in tupleAsTypeTuple.data.indices) {
                        throw ArrayIndexOutOfBoundsException(
                            "Error at line ${expr.line_num}:\n" +
                                    "Tuple has nothing at position $position!"
                        )
                    }
                } else {
                    throwTypeError(
                        lineNumber = expr.line_num,
                        expectedType = Types.Tuple(listOf()),
                        actualType = tuple,
                        expr = "Dot tuple",
                    )
                }

                tupleAsTypeTuple!!.data[position - 1]
            }

            is Match -> {
                val variableType = parseExpr(expr.expr_, context)

                parseMatchCase(expr.listmatchcase_, variableType, context)
            }

            is Inl -> {
                Types.Sum(parseExpr(expr.expr_, context), Types.Undefined)
            }

            is Inr -> {
                Types.Sum(Types.Undefined, parseExpr(expr.expr_, context))
            }

            is Record -> {
                val recordParams = mutableMapOf<String, Types>()

                for (binding in expr.listbinding_) {
                    if (binding is ABinding) {
                        val name = binding.stellaident_
                        val type = parseExpr(binding.expr_, context)

                        recordParams[name] = type
                    } else {
                        throw InvalidTypeException("Invalid type of Binding field!")
                    }
                }

                Types.Record(recordParams)
            }

            is DotRecord -> {
                val paramName = expr.stellaident_
                val variable = parseExpr(expr.expr_, context)


                if (variable is Types.Record) {
                    if (!variable.data.contains(paramName)) {
                        throw NoSuchFieldError("No field with name $paramName in $variable")
                    }
                } else {
                    throwTypeError(
                        lineNumber = expr.line_num,
                        expectedType = Types.Record(mutableMapOf()),
                        actualType = variable,
                        expr = "Dot record",
                    )
                }

                (variable as Types.Record).data[paramName]!!
            }

            is Assign -> {
                val variable = parseExpr(expr.expr_1, context)
                val resultExpr = parseExpr(expr.expr_2, context)

                if (variable !is Types.Ref) {
                    throwTypeError(
                        lineNumber = expr.line_num,
                        expectedType = Types.Ref(Types.Undefined),
                        actualType = variable,
                        expr = "Assigning to non-reference",
                    )
                } else {
                    if (variable.content::class != resultExpr::class) {
                        throwTypeError(
                            lineNumber = expr.line_num,
                            expectedType = variable,
                            actualType = resultExpr,
                            expr = "Assigning to a different type of variable",
                        )
                    }
                }

                Types.Unit
            }

            is Deref -> {
                val variable = parseExpr(expr.expr_, context)

                if (variable !is Types.Ref) {
                    throwTypeError(
                        lineNumber = expr.line_num,
                        expectedType = Types.Ref(Types.Undefined),
                        actualType = variable,
                        expr = "Derefencing of non-reference",
                    )
                }

                (variable as Types.Ref).content
            }

            is Ref -> {
                val initialType = parseExpr(expr.expr_, context)

                Types.Ref(initialType)
            }

            is Sequence -> {
                parseExpr(expr.expr_1, context)
                parseExpr(expr.expr_2, context)
            }

            is TypeAbstraction -> {
                val func = parseExpr(expr.expr_, context)
                val paramName = expr.liststellaident_.first

                if (func !is Types.Fun) {
                    throwTypeError(
                        lineNumber = expr.line_num,
                        expectedType = Types.Fun(mutableListOf(), Types.Undefined, Types.Undefined),
                        actualType = func,
                        expr = "Type abstraction"
                    )
                }

                // create new generic function, as TypeAbstraction is
                // the function requires type from paramNames to be evaluated
                (func as Types.Fun).copy(
                    paramNames = func.paramNames.apply {
                        add(paramName)
                    }
                )
            }

            is TypeApplication -> {
                // get list of applied types
                val types = mutableListOf<Types>()
                for (type in expr.listtype_) {
                    types.add(parseType(type))
                }

                // cast inner expression type
                val exprType = parseExpr(expr.expr_, context)
                val typeAsGenericFun = exprType as? Types.Fun

                // check if the function is generic
                if (typeAsGenericFun?.paramNames?.isNotEmpty() == false) {
                    throwTypeError(
                        lineNumber = expr.line_num,
                        expectedType = Types.Fun(mutableListOf(), Types.Undefined, Types.Undefined),
                        actualType = exprType,
                        expr = "Type application"
                    )
                }

                // evaluate inner part with substituted types
                val newContext = context.also {
                    typeAsGenericFun!!.paramNames.forEachIndexed { index, s ->
                        context.genericContext[s] = types[index]
                    }
                }

                getGenericSetupType(typeAsGenericFun!!, newContext)
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
        return when (type) {
            is TypeFun -> {
                val paramType = parseType(type.listtype_.first)
                val returnType = parseType(type.type_)

                Types.Fun(mutableListOf(), paramType, returnType)
            }

            is TypeTuple -> {
                val params = mutableListOf<Types>()

                for (param in type.listtype_) {
                    params.add(parseType(param))
                }

                Types.Tuple(params)
            }

            is TypeSum -> {
                val firstParam = parseType(type.type_1)
                val secondParam = parseType(type.type_2)

                return Types.Sum(firstParam, secondParam)
            }

            is TypeRecord -> {
                val params = mutableMapOf<String, Types>()

                for (param in type.listrecordfieldtype_) {
                    if (param is ARecordFieldType) {
                        val name = param.stellaident_
                        val type = parseType(param.type_)

                        params[name] = type
                    } else {
                        throw InvalidTypeException("Invalid type of TypeRecord field!")
                    }
                }

                Types.Record(params)
            }

            is TypeRef -> {
                val param = parseType(type.type_)

                Types.Ref(param)
            }

            is TypeForAll -> {
                val params = mutableListOf<String>()
                for (paramName in type.liststellaident_) {
                    params.add(paramName)
                }

                val func = parseType(type.type_) as? Types.Fun

                if (func != null) {
                    Types.Fun(params, func.inputType, func.outputType)
                } else {
                    throw InvalidTypeException("Invalid type of Func in Generic function field!")
                }
            }

            is TypeVar -> {
                Types.Var(type.stellaident_)
            }

            else -> {
                Types.getBaseType(type)
            }
        }
    }

    /**
     * Parse given Match expression data.
     *
     * @param listMatchCase the possible cases of Match expression
     * @param varType type of the variable Match is applied on
     * @param context current context of execution
     *
     * @return type of the match cases output, making sure all branches return the same type
     *
     * @throws InvalidTypeException in cases:
     *  1. type of variable, which match is applied on, is not of type Sum
     *  2. different branches return different types of output
     *  3. invalid MatchCase field
     *  4. MatchCase is applied on Pattern apart from PatternInl or PatternInr
     */
    private fun parseMatchCase(listMatchCase: ListMatchCase, varType: Types, context: FunctionContext): Types {
        var returnType: Types = Types.Undefined

        if (varType !is Types.Sum) {
            throw InvalidTypeException("Match expression is applied on variable of type $varType when Sum is expected!")
        }

        for (matchCase in listMatchCase) {
            if (matchCase is AMatchCase) {

                when (val pattern = matchCase.pattern_) {

                    // check the first parameter of Sum
                    // run through the expressions under it, checking correctness of types
                    is PatternInl -> {
                        val variableName = getPatternVariableName(pattern.pattern_)

                        // append the context with the variable, introduced by the pattern
                        val newContext = context.also {
                            it.currentFuncParams[variableName] = varType.first
                        }

                        val actualReturnType = parseExpr(matchCase.expr_, newContext)

                        // check if the return type of the branch is the same for all branches
                        if (returnType == Types.Undefined || returnType == actualReturnType) {
                            returnType = actualReturnType
                        } else {
                            throwTypeError(
                                lineNumber = pattern.line_num,
                                expectedType = returnType,
                                actualType = actualReturnType,
                                expr = "Branch of Match expression",
                            )
                        }
                    }

                    // mostly the same logic, but assuming we are dealing with the second parameter of Sum
                    is PatternInr -> {
                        val variableName = getPatternVariableName(pattern.pattern_)

                        val newContext = context.also {
                            it.currentFuncParams[variableName] = varType.second
                        }

                        val actualReturnType = parseExpr(matchCase.expr_, newContext)

                        if (returnType == Types.Undefined || returnType == actualReturnType) {
                            returnType = actualReturnType
                        } else {
                            throwTypeError(
                                lineNumber = pattern.line_num,
                                expectedType = returnType,
                                actualType = actualReturnType,
                                expr = "Branch of Match expression",
                            )
                        }
                    }

                    else -> {
                        throw Exception("Match case pattern must be either PatternInl or PatternInr!")
                    }
                }
            } else {
                throw Exception("Invalid type of Match branch!")
            }
        }

        return returnType
    }

    /**
     * Get the variable name from the pattern.
     *
     * @param pattern PatternInl or PatternInr to get variable name from
     *
     * @throws InvalidTypeException if given pattern is not of type PatternVar
     */
    private fun getPatternVariableName(pattern: Pattern): String {
        return if (pattern is PatternVar) {
            pattern.stellaident_
        } else {
            throw InvalidTypeException("Given pattern in not PatternVar!")
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
        if (actualType == Types.Panic) {
            throw Exception("Panic!!!")
        }

        throw InvalidTypeException(
            "Error at line $lineNumber: \n" +
                    "Expected type $expectedType but got $actualType\n" +
                    "For $expr"
        )
    }

    /**
     * Substitute the declared generic types into the given expression
     */
    private fun getGenericSetupType(type: Types, context: FunctionContext): Types {
        return when (type) {
            is Types.Var -> {
                val selectedType = context.genericContext.getOrDefault(type.paramName, type)

                selectedType
            }

            is Types.Fun -> {
                val inputType = getGenericSetupType(type.inputType, context)
                val outputType = getGenericSetupType(type.outputType, context)

                Types.Fun(type.paramNames, inputType, outputType)
            }

            is Types.Record -> {
                val newData = type.data

                for ((key, value) in newData) {
                    newData[key] = getGenericSetupType(value, context)
                }

                Types.Record(newData)
            }

            is Types.Sum -> {
                val firstVar = getGenericSetupType(type.first, context)
                val secondVar = getGenericSetupType(type.second, context)

                Types.Sum(firstVar, secondVar)
            }

            is Types.Ref -> {
                val content = getGenericSetupType(type.content, context)

                Types.Ref(content)
            }

            is Types.Tuple -> {
                val data = mutableListOf<Types>()

                for (element in type.data) {
                    data.add(getGenericSetupType(element, context))
                }

                Types.Tuple(data)
            }

            else -> {
                type
            }
        }
    }

    /**
     * Context class for program.
     * It stores the description of the function by its name.
     * It stores the type of the variable by its name.
     * It stores the introduced generic types by its name.
     */
    private class FunctionContext {
        val funcNameToFuncType = mutableMapOf<String, Types.Fun>()
        val currentFuncParams = mutableMapOf<String, Types>()
        val genericContext = mutableMapOf<String, Types>()
    }
}