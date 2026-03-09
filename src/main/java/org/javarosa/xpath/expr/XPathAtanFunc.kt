package org.javarosa.xpath.expr

import org.javarosa.core.model.condition.EvaluationContext
import org.javarosa.core.model.instance.DataInstance
import org.javarosa.xpath.parser.XPathSyntaxException
import kotlin.math.atan

class XPathAtanFunc : XPathFuncExpr {

    constructor() {
        name = NAME
        expectedArgCount = EXPECTED_ARG_COUNT
    }

    @Throws(XPathSyntaxException::class)
    constructor(args: Array<XPathExpression>) : super(NAME, args, EXPECTED_ARG_COUNT, true)

    override fun evalBody(
        model: DataInstance<*>?, evalContext: EvaluationContext, evaluatedArgs: Array<Any>): Any {
        return atan(FunctionUtils.toDouble(evaluatedArgs[0]))
    }

    companion object {
        @JvmField
        val NAME = "atan"
        private const val EXPECTED_ARG_COUNT = 1
    }
}
