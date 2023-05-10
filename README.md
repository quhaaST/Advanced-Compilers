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
- Unit type (nodes TypeUnit, ConstUnit)
- Tuples of any size (nodes TypeTuple, Tuple, DotTuple)
- Sum types (nodes TypeSum, Inl, Inr, Match, MatchCase, PatternInl, PatternInr, PatternVar)
- Sequencing (node Sequence)
- References (nodes TypeRef, Ref, Deref, Assign)
- Errors (node Panic)
- Records (nodes TypeRecord, ARecordFieldType, Record, DotRecord 
- Subtyping for records (with permutations of fields) and functions
- Type variables (node TypeVar)
- Universal types (nodes TypeForAll, TypeAbstraction, TypeApplication)
- Generic function declarations (DeclFunGeneric)


The main logic for TypeChecker is contained in `ProgramTypeChecker` class.

The list of tests is extended:
- 2 new `ill-typed-tests`:
  - `missing-main-method.stella` - check for presence of `main` method
  - `undefined-function.stella` - check for using undeclared function
- 2 new `well-typed tests`:
  - `biconditional.stella` - implements the biconditional logical operator
  - `power-of-two.stella` - raises the 2 into the power of given parameter

Part of the tests were excluded from the final test-case block, since extensions are not asked to be supported:
 - `#comparison-operators`
 - `#arithmetic-operators`
 - `#let-bindings`
 - `#natural-literals`
 - `#type-cast`
 - `#top-type`

# How to setup
1. Build the project using `./gradlew build` command.
2. To run the tests, use `./gradlew test`


If any questions, feel free to contact me `a.evdokimov@innopolis.university` or in telegram `@quhaast`
