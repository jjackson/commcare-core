package org.javarosa.xpath.expr

import org.javarosa.core.model.condition.EvaluationContext
import org.javarosa.core.model.instance.DataInstance
import org.javarosa.xpath.parser.XPathSyntaxException
import kotlin.math.atan2

class XPathAtanTwoFunc : XPathFuncExpr {

    constructor() {
        name = NAME
        expectedArgCount = EXPECTED_ARG_COUNT
    }

    @Throws(XPathSyntaxException::class)
    constructor(args: Array<XPathExpression>) : super(NAME, args, EXPECTED_ARG_COUNT, true)

    override fun evalBody(
        model: DataInstance<*>?, evalContext: EvaluationContext, evaluatedArgs: Array<Any>): Any {
        val value1 = FunctionUtils.toDouble(evaluatedArgs[0])
        val value2 = FunctionUtils.toDouble(evaluatedArgs[1])
        return atan2(value1, value2)
    }

    companion object {
        @JvmField
        val NAME = "atan2"
        private const val EXPECTED_ARG_COUNT = 2
    }
}
