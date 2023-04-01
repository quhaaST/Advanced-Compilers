package org.syntax.stella.Absyn

import kotlin.collections.List

sealed class Types {
    object Bool : Types()

    object Nat: Types()

    object Var: Types()

    object Unit: Types()

    data class Tuple(
        val data: List<Types>,
    ) : Types()

    data class Fun(
        val inputType: Types,
        val outputType: Types
    ) : Types()

    object Undefined: Types()

    companion object {
        fun getBaseType(type: Type): Types {
            return when(type) {
                is TypeBool -> Bool
                is TypeNat -> Nat
                is TypeVar -> Var
                is TypeUnit -> Unit
                else -> Undefined
            }
        }
    }

    override fun toString(): String {
        return when (this) {
            is Bool -> "Bool"
            is Nat -> "Nat"
            is Var -> "Var"
            is Unit -> "Unit"
            is Tuple -> "Tuple {${data.joinToString()}}"
            is Fun -> "Fun $inputType -> $outputType"
            is Undefined -> "Undefined"
        }
    }
}