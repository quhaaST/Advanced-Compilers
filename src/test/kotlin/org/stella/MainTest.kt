package org.stella

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.io.File
import java.io.FileInputStream
import java.io.IOException

const val baseDir = "src/test/resources"
const val coreBaseDir = "$baseDir/core"
const val listsBaseDir = "$baseDir/lists"
const val pairsBaseDir = "$baseDir/pairs"
const val recordsBaseDir = "$baseDir/records"
const val sumTypesBaseDir = "$baseDir/sum-types"
const val tuplesBaseDir = "$baseDir/tuples"
const val variantsBaseDir = "$baseDir/variants"

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

    @ParameterizedTest(name = "{index} Typechecking well-typed lists program {0}")
    @ValueSource(strings = [
        "$listsBaseDir/well-typed/lists-1.stella",
    ])
    @Throws(
        IOException::class,
        Exception::class
    )
    fun testWellTypedLists(filepath: String) {
        val original = System.`in`
        val fips = FileInputStream(File(filepath))
        System.setIn(fips)
        main()
        System.setIn(original)
    }

    @ParameterizedTest(name = "{index} Typechecking ill-typed lists program {0}")
    @ValueSource(strings = [
        "$listsBaseDir/ill-typed/bad-variants-1.stella",
    ])
    @Throws(
        IOException::class,
        Exception::class
    )
    fun testIllTypedLists(filepath: String) {
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