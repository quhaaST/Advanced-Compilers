# stella-with-simple-extensions
### Implemented by Evdokimov Aleksandr, B20-SD1


This is the implementation of the basic Stella-core typechecker.

For the following moment, it is possible to typecheck:
-AProgram
- DeclFun (only function name, one parameter, return type, and return expression)
- TypeBool, TypeNat, and TypeFun
- boolean expressions: ConstTrue, ConstFalse, If
- anonymous functions: Abstraction
- function application: Application
- natural number expressions: ConstInt(0), Succ, NatRec
- variables: Var

The main logic for TypeChecker is contained in `ProgramTypeChecker` class.

The list of tests is extended:
- 2 new `ill-typed-tests`:
  - `missing-main-method.stella` - check for presence of `main` method
  - `undefined-function.stella` - check for using undeclared function
- 2 new `well-typed tests`:
  - `biconditional.stella` - implements the biconditional logical operator
  - `power-of-two.stella` - raises the 2 into the power of given parameter

# How to setup
1. Build the project using `./gradlew build` command.
2. To run the tests, use `./gradlew test`


If any questions, feel free to contact me `a.evdokimov@innopolis.university` or in telegram `@quhaast`
