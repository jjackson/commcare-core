package org.javarosa.xpath.expr

import org.javarosa.core.model.condition.EvaluationContext
import org.javarosa.core.model.instance.DataInstance
import org.javarosa.xpath.XPathNodeset
import org.javarosa.xpath.XPathTypeMismatchException
import org.javarosa.xpath.parser.XPathSyntaxException

class XPathSumFunc : XPathFuncExpr {

    constructor() {
        name = NAME
        expectedArgCount = EXPECTED_ARG_COUNT
    }

    @Throws(XPathSyntaxException::class)
    constructor(args: Array<XPathExpression>) : super(NAME, args, EXPECTED_ARG_COUNT, true)

    override fun evalBody(
        model: DataInstance<*>?, evalContext: EvaluationContext, evaluatedArgs: Array<Any>): Any {
        if (evaluatedArgs[0] is XPathNodeset) {
            @Suppress("UNCHECKED_CAST")
            return sum((evaluatedArgs[0] as XPathNodeset).toArgList() as Array<Any>)
        } else {
            throw XPathTypeMismatchException("uses an invalid reference inside a sum function")
        }
    }

    companion object {
        @JvmField
        val NAME = "sum"
        private const val EXPECTED_ARG_COUNT = 1

        /**
         * sum the values in a nodeset; each element is coerced to a numeric value
         */
        private fun sum(argVals: Array<Any>): Double {
            var sum = 0.0
            for (argVal in argVals) {
                sum += FunctionUtils.toNumeric(argVal)
            }
            return sum
        }
    }
}
