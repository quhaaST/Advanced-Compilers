package org.syntax.stella.Absyn

import kotlin.collections.List

sealed class Types {
    object Bool : Types()

    object Nat : Types()

    data class Var(
        val paramName: String,
    ) : Types()

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
        val paramNames: MutableList<String> = mutableListOf(),
        val inputType: Types,
        val outputType: Types
    ) : Types() {
        override fun equals(other: Any?): Boolean {
            return (other is Fun) &&
                    (this.inputType == other.inputType) &&
                    (this.outputType == other.outputType)
        }

        override fun hashCode(): Int {
            var result = paramNames.hashCode()
            result = 31 * result + inputType.hashCode()
            result = 31 * result + outputType.hashCode()
            return result
        }

    }

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

    data class Ref(
        val content: Types,
    ) : Types()

    object Undefined : Types()

    companion object {
        fun getBaseType(type: Type): Types {
            return when (type) {
                is TypeBool -> Bool
                is TypeNat -> Nat
                is TypeUnit -> Unit
                else -> Undefined
            }
        }
    }

    override fun toString(): String {
        return when (this) {
            Bool -> "Bool"
            Nat -> "Nat"
            is Var -> "Var associated with $paramName"
            Unit -> "Unit"
            is Tuple -> "Tuple {${data.joinToString()}}"
            is Sum -> "Sum of $first and $second"
            is Fun -> "Fun $inputType -> $outputType, on params $paramNames"
            is Record -> "Record with field $data"
            Panic -> "Panic!"
            is Ref -> "Reference of type $content"
            is Undefined -> "Undefined"
        }
    }
}