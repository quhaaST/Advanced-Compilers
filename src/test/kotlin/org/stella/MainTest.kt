package org.stella

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.io.File
import java.io.FileInputStream
import java.io.IOException

const val baseDir = "src/test/resources"
const val coreBaseDir = "$baseDir/core"
const val referencesBaseDir = "$baseDir/references"
const val pairsBaseDir = "$baseDir/pairs"
const val recordsBaseDir = "$baseDir/records"
const val sumTypesBaseDir = "$baseDir/sum-types"
const val tuplesBaseDir = "$baseDir/tuples"
const val exceptionsBaseDir = "$baseDir/exceptions"
const val subtypingBaseDir = "$baseDir/subtyping"

internal class MainTest {
    @ParameterizedTest(name = "{index} Typechecking well-typed core program {0}")
    @ValueSource(strings = [
        "$coreBaseDir/well-typed/factorial.stella",
        "$coreBaseDir/well-typed/squares.stella",
        "$coreBaseDir/well-typed/higher-order-1.stella",
        "$coreBaseDir/well-typed/increment_twice.stella",
        "$coreBaseDir/well-typed/logical-operators.stella",
        "$coreBaseDir/well-typed/biconditional.stella",
        "$coreBaseDir/well-typed/power-of-two.stella",
    ])
    @Throws(
        IOException::class,
        Exception::class
    )
    fun testWellTypedCore(filepath: String) {
        val original = System.`in`
        val fips = FileInputStream(File(filepath))
        System.setIn(fips)
        main()
        System.setIn(original)
    }

    @ParameterizedTest(name = "{index} Typechecking ill-typed core program {0}")
    @ValueSource(strings = [
        "$coreBaseDir/ill-typed/applying-non-function-1.stella",
        "$coreBaseDir/ill-typed/applying-non-function-2.stella",
        "$coreBaseDir/ill-typed/applying-non-function-3.stella",
        "$coreBaseDir/ill-typed/argument-type-mismatch-1.stella",
        "$coreBaseDir/ill-typed/argument-type-mismatch-2.stella",
        "$coreBaseDir/ill-typed/argument-type-mismatch-3.stella",
        "$coreBaseDir/ill-typed/bad-if-1.stella",
        "$coreBaseDir/ill-typed/bad-if-2.stella",
        "$coreBaseDir/ill-typed/bad-succ-1.stella",
        "$coreBaseDir/ill-typed/bad-succ-2.stella",
        "$coreBaseDir/ill-typed/bad-succ-3.stella",
        "$coreBaseDir/ill-typed/shadowed-variable-1.stella",
        "$coreBaseDir/ill-typed/undefined-variable-1.stella",
        "$coreBaseDir/ill-typed/undefined-variable-2.stella",
        "$coreBaseDir/ill-typed/bad-squares-1.stella",
        "$coreBaseDir/ill-typed/bad-squares-2.stella",
        "$coreBaseDir/ill-typed/missing-main-method.stella",
        "$coreBaseDir/ill-typed/undefined-function.stella",
    ])
    @Throws(
        IOException::class,
        Exception::class
    )
    fun testIllTypedCore(filepath: String) {
        val original = System.`in`
        val fips = FileInputStream(File(filepath))
        System.setIn(fips)
        var typecheckerFailed = false
        try {
            main()
        } catch (e: java.lang.Exception) {
            typecheckerFailed = true
        }
        if (!typecheckerFailed) {
            throw java.lang.Exception("expected the typechecker to fail!")
        }        // TODO: check that there is a type error actually, and not a problem with implementation
        System.setIn(original)
    }

    @ParameterizedTest(name = "{index} Typechecking well-typed pairs program {0}")
    @ValueSource(strings = [
        "$pairsBaseDir/well-typed/pairs-1.stella",
    ])
    @Throws(
        IOException::class,
        Exception::class
    )
    fun testWellTypedPairs(filepath: String) {
        val original = System.`in`
        val fips = FileInputStream(File(filepath))
        System.setIn(fips)
        main()
        System.setIn(original)
    }

    @ParameterizedTest(name = "{index} Typechecking ill-typed pairs program {0}")
    @ValueSource(strings = [
        "$pairsBaseDir/ill-typed/bad-pairs-1.stella",
    ])
    @Throws(
        IOException::class,
        Exception::class
    )
    fun testIllTypedPairs(filepath: String) {
        val original = System.`in`
        val fips = FileInputStream(File(filepath))
        System.setIn(fips)
        var typecheckerFailed = false
        try {
            main()
        } catch (e: java.lang.Exception) {
            typecheckerFailed = true
        }
        if (!typecheckerFailed) {
            throw java.lang.Exception("expected the typechecker to fail!")
        }        // TODO: check that there is a type error actually, and not a problem with implementation
        System.setIn(original)
    }

    @ParameterizedTest(name = "{index} Typechecking well-typed sum types program {0}")
    @ValueSource(strings = [
        "$sumTypesBaseDir/well-typed/sum-types-1.stella",
        "$sumTypesBaseDir/well-typed/sum-types-2.stella",
    ])
    @Throws(
        IOException::class,
        Exception::class
    )
    fun testWellTypedSumTypes(filepath: String) {
        val original = System.`in`
        val fips = FileInputStream(File(filepath))
        System.setIn(fips)
        main()
        System.setIn(original)
    }

    @ParameterizedTest(name = "{index} Typechecking ill-typed sum types program {0}")
    @ValueSource(strings = [
        "$sumTypesBaseDir/ill-typed/bad-sum-types-1.stella",
    ])
    @Throws(
        IOException::class,
        Exception::class
    )
    fun testIllTypedSumTypes(filepath: String) {
        val original = System.`in`
        val fips = FileInputStream(File(filepath))
        System.setIn(fips)
        var typecheckerFailed = false
        try {
            main()
        } catch (e: java.lang.Exception) {
            typecheckerFailed = true
        }
        if (!typecheckerFailed) {
            throw java.lang.Exception("expected the typechecker to fail!")
        }        // TODO: check that there is a type error actually, and not a problem with implementation
        System.setIn(original)
    }

    @ParameterizedTest(name = "{index} Typechecking well-typed tuples program {0}")
    @ValueSource(strings = [
        "$tuplesBaseDir/well-typed/tuples-1.stella",
    ])
    @Throws(
        IOException::class,
        Exception::class
    )
    fun testWellTypedTuples(filepath: String) {
        val original = System.`in`
        val fips = FileInputStream(File(filepath))
        System.setIn(fips)
        main()
        System.setIn(original)
    }

    @ParameterizedTest(name = "{index} Typechecking ill-typed tuples program {0}")
    @ValueSource(strings = [
        "$tuplesBaseDir/ill-typed/bad-tuples-1.stella",
    ])
    @Throws(
        IOException::class,
        Exception::class
    )
    fun testIllTypedTuples(filepath: String) {
        val original = System.`in`
        val fips = FileInputStream(File(filepath))
        System.setIn(fips)
        var typecheckerFailed = false
        try {
            main()
        } catch (e: java.lang.Exception) {
            typecheckerFailed = true
        }
        if (!typecheckerFailed) {
            throw java.lang.Exception("expected the typechecker to fail!")
        }        // TODO: check that there is a type error actually, and not a problem with implementation
        System.setIn(original)
    }

    @ParameterizedTest(name = "{index} Typechecking well-typed references program {0}")
    @ValueSource(strings = [
        "$referencesBaseDir/well-typed/refs-1.stella",
        "$referencesBaseDir/well-typed/refs-2.stella",
//        "$referencesBaseDir/well-typed/refs-3.stella",
    ])
    @Throws(
        IOException::class,
        Exception::class
    )
    fun testWellTypedReferences(filepath: String) {
        val original = System.`in`
        val fips = FileInputStream(File(filepath))
        System.setIn(fips)
        main()
        System.setIn(original)
    }

    @ParameterizedTest(name = "{index} Typechecking ill-typed references program {0}")
    @ValueSource(strings = [
        "$referencesBaseDir/ill-typed/bad-refs-1.stella",
        "$referencesBaseDir/ill-typed/bad-refs-2.stella",
//        "$referencesBaseDir/ill-typed/bad-refs-3.stella",
    ])
    @Throws(
        IOException::class,
        Exception::class
    )
    fun testIllTypedReferences(filepath: String) {
        val original = System.`in`
        val fips = FileInputStream(File(filepath))
        System.setIn(fips)
        var typecheckerFailed = false
        try {
            main()
        } catch (e: java.lang.Exception) {
            typecheckerFailed = true
        }
        if (!typecheckerFailed) {
            throw java.lang.Exception("expected the typechecker to fail!")
        }        // TODO: check that there is a type error actually, and not a problem with implementation
        System.setIn(original)
    }

    @ParameterizedTest(name = "{index} Typechecking well-typed exceptions program {0}")
    @ValueSource(strings = [
//        "$exceptionsBaseDir/well-typed/panic-1.stella",
//        "$exceptionsBaseDir/well-typed/panic-2.stella",
        "$exceptionsBaseDir/well-typed/panic-3.stella",
    ])
    @Throws(
        IOException::class,
        Exception::class
    )
    fun testWellTypedExceptions(filepath: String) {
        val original = System.`in`
        val fips = FileInputStream(File(filepath))
        System.setIn(fips)
        var typecheckerFailed = false
        try {
            main()
        } catch (e: java.lang.Exception) {
            typecheckerFailed = true
        }
        if (!typecheckerFailed) {
            throw java.lang.Exception("expected the typechecker to fail!")
        }        // TODO: check that there is a type error actually, and not a problem with implementation
        System.setIn(original)
    }

    @ParameterizedTest(name = "{index} Typechecking ill-typed exceptions program {0}")
    @ValueSource(strings = [
        "$exceptionsBaseDir/ill-typed/bad-panic-1.stella",
    ])
    @Throws(
        IOException::class,
        Exception::class
    )
    fun testIllTypedExceptions(filepath: String) {
        val original = System.`in`
        val fips = FileInputStream(File(filepath))
        System.setIn(fips)
        var typecheckerFailed = false
        try {
            main()
        } catch (e: java.lang.Exception) {
            typecheckerFailed = true
        }
        if (!typecheckerFailed) {
            throw java.lang.Exception("expected the typechecker to fail!")
        }        // TODO: check that there is a type error actually, and not a problem with implementation
        System.setIn(original)
    }

    @ParameterizedTest(name = "{index} Typechecking well-typed records program {0}")
    @ValueSource(strings = [
        "$recordsBaseDir/well-typed/records-1.stella",
    ])
    @Throws(
        IOException::class,
        Exception::class
    )
    fun testWellTypedRecords(filepath: String) {
        val original = System.`in`
        val fips = FileInputStream(File(filepath))
        System.setIn(fips)
        main()
        System.setIn(original)
    }

    @ParameterizedTest(name = "{index} Typechecking ill-typed records program {0}")
    @ValueSource(strings = [
        "$recordsBaseDir/ill-typed/bad-records-1.stella",
    ])
    @Throws(
        IOException::class,
        Exception::class
    )
    fun testIllTypedRecords(filepath: String) {
        val original = System.`in`
        val fips = FileInputStream(File(filepath))
        System.setIn(fips)
        var typecheckerFailed = false
        try {
            main()
        } catch (e: java.lang.Exception) {
            typecheckerFailed = true
        }
        if (!typecheckerFailed) {
            throw java.lang.Exception("expected the typechecker to fail!")
        }        // TODO: check that there is a type error actually, and not a problem with implementation
        System.setIn(original)
    }

    @ParameterizedTest(name = "{index} Typechecking well-typed subtyping program {0}")
    @ValueSource(strings = [
        "$subtypingBaseDir/well-typed/subtyping-1.stella",
        "$subtypingBaseDir/well-typed/subtyping-2.stella",
        "$subtypingBaseDir/well-typed/subtyping-3.stella",
        "$subtypingBaseDir/well-typed/subtyping-4.stella",
        "$subtypingBaseDir/well-typed/subtyping-6.stella",
    ])
    @Throws(
        IOException::class,
        Exception::class
    )
    fun testWellTypedSubtyping(filepath: String) {
        val original = System.`in`
        val fips = FileInputStream(File(filepath))
        System.setIn(fips)
        main()
        System.setIn(original)
    }

    @ParameterizedTest(name = "{index} Typechecking ill-typed subtyping program {0}")
    @ValueSource(strings = [
        "$subtypingBaseDir/ill-typed/bad-subtyping-1.stella",
        "$subtypingBaseDir/ill-typed/bad-subtyping-3.stella",
        "$subtypingBaseDir/ill-typed/bad-subtyping-4.stella",
        "$subtypingBaseDir/ill-typed/bad-subtyping-5.stella",
    ])
    @Throws(
        IOException::class,
        Exception::class
    )
    fun testIllTypedSubtyping(filepath: String) {
        val original = System.`in`
        val fips = FileInputStream(File(filepath))
        System.setIn(fips)
        var typecheckerFailed = false
        try {
            main()
        } catch (e: java.lang.Exception) {
            typecheckerFailed = true
        }
        if (!typecheckerFailed) {
            throw java.lang.Exception("expected the typechecker to fail!")
        }        // TODO: check that there is a type error actually, and not a problem with implementation
        System.setIn(original)
    }
}