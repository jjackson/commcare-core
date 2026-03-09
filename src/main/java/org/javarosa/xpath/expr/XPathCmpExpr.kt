package org.javarosa.xpath.expr

import org.javarosa.core.model.condition.EvaluationContext
import org.javarosa.core.model.condition.pivot.CmpPivot
import org.javarosa.core.model.condition.pivot.UnpivotableExpressionException
import org.javarosa.core.model.data.DecimalData
import org.javarosa.core.model.data.UncastData
import org.javarosa.core.model.instance.DataInstance
import org.javarosa.xpath.XPathNodeset
import java.util.Vector

class XPathCmpExpr : XPathBinaryOpExpr {

    @Suppress("unused")
    constructor() // for deserialization

    constructor(op: Int, a: XPathExpression, b: XPathExpression) : super(op, a, b)

    override fun evalRaw(model: DataInstance<*>?, evalContext: EvaluationContext): Any {
        val aval = FunctionUtils.toNumeric(a.eval(model, evalContext))
        val bval = FunctionUtils.toNumeric(b.eval(model, evalContext))

        val fa = aval as Double
        val fb = bval as Double

        val result = when (op) {
            LT -> fa < fb
            GT -> fa > fb
            LTE -> fa <= fb
            GTE -> fa >= fb
            else -> false
        }
        return result
    }

    override fun toString(): String {
        val sOp = when (op) {
            LT -> "<"
            GT -> ">"
            LTE -> "<="
            GTE -> ">="
            else -> null
        }
        return super.toString(sOp)
    }

    @Throws(UnpivotableExpressionException::class)
    override fun pivot(
        model: DataInstance<*>,
        evalContext: EvaluationContext,
        pivots: Vector<Any>,
        sentinal: Any
    ): Any? {
        val aval = a.pivot(model, evalContext, pivots, sentinal)
        var bval = b.pivot(model, evalContext, pivots, sentinal)
        if (bval is XPathNodeset) {
            bval = bval.unpack()
        }

        if (handled(aval, bval, sentinal, pivots) || handled(bval, aval, sentinal, pivots)) {
            return null
        }

        return this.eval(model, evalContext)
    }

    @Throws(UnpivotableExpressionException::class)
    private fun handled(a: Any?, b: Any?, sentinal: Any, pivots: Vector<Any>): Boolean {
        if (sentinal === a) {
            if (b == null) {
                // Can't pivot on an expression which is derived from pivoted expressions
                throw UnpivotableExpressionException()
            } else if (sentinal === b) {
                // WTF?
                throw UnpivotableExpressionException()
            } else {
                val value: Double? = when (b) {
                    is Double -> b
                    is Int -> b.toDouble()
                    is Long -> b.toDouble()
                    is Float -> b.toDouble()
                    is Short -> b.toDouble()
                    is Byte -> b.toDouble()
                    is String -> {
                        try {
                            // TODO: Too expensive?
                            DecimalData().cast(UncastData(b)).getValue() as Double
                        } catch (e: Exception) {
                            throw UnpivotableExpressionException(
                                "Unrecognized numeric data in cmp expression: $b"
                            )
                        }
                    }
                    else -> throw UnpivotableExpressionException(
                        "Unrecognized numeric data in cmp expression: $b"
                    )
                }

                pivots.addElement(CmpPivot(value!!, op))
                return true
            }
        }
        return false
    }

    override fun toPrettyString(): String {
        val prettyA = a.toPrettyString()
        val prettyB = b.toPrettyString()
        val opString = when (op) {
            LT -> " < "
            GT -> " > "
            LTE -> " <= "
            GTE -> " >= "
            else -> return "unknown_operator($prettyA, $prettyB)"
        }
        return prettyA + opString + prettyB
    }

    override fun equals(other: Any?): Boolean {
        return (this === other) ||
                ((other is XPathCmpExpr) && binOpEquals(other))
    }

    companion object {
        @JvmField
        val LT = 0
        @JvmField
        val GT = 1
        @JvmField
        val LTE = 2
        @JvmField
        val GTE = 3
    }
}
