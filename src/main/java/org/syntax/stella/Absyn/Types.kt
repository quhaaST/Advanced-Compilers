package org.syntax.stella.Absyn

import kotlin.collections.List

sealed class Types {
    object Bool : Types()

    object Nat : Types()

    object Var : Types()

    object Unit : Types()

    data class Tuple(
        val data: List<Types>,
    ) : Types()

    data class Sum(
        val first: Types,
        val second: Types,
    ) : Types() {
        // Override the base equals functions as different Sum types equality is defined by inl() or inr()
        override fun equals(other: Any?): Boolean {
            return (other is Sum) &&
                    ((other.first == this.first && other.first !is Undefined) ||
                            (other.second == this.second && other.second !is Undefined))
        }

        override fun hashCode(): Int {
            var result = first.hashCode()
            result = 31 * result + second.hashCode()
            return result
        }
    }

    data class Fun(
        val inputType: Types,
        val outputType: Types
    ) : Types()

    data class Record(
        val data: MutableMap<String, Types>
    ) : Types() {
        override fun equals(other: Any?): Boolean {
            return (other is Record) &&
                    (data.all { (name, value) -> other.data[name] == value })
        }

        override fun hashCode(): Int {
            return data.hashCode()
        }
    }

    object Panic : Types()

    object Undefined : Types()

    companion object {
        fun getBaseType(type: Type): Types {
            return when (type) {
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
            is Sum -> "Sum of $first and $second"
            is Fun -> "Fun $inputType -> $outputType"
            is Record -> "Record with field $data"
            is Panic -> "Panic!"
            is Undefined -> "Undefined"
        }
    }
}