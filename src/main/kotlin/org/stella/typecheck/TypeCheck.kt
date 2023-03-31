package org.stella.typecheck

import org.syntax.stella.Absyn.Program

/**
 * Base class for starting the TypeCheck process of the program.
 * All the logic is contained in [ProgramTypeChecker].
 */
object TypeCheck {
    @Throws(Exception::class)
    fun typecheckProgram(program: Program) {
        val programTypechecker = ProgramTypeChecker()

        // launch the type checking process
        programTypechecker.startTypeCheck(program)
    }
}
