package org.javarosa.xpath.expr

import org.javarosa.core.model.condition.EvaluationContext
import org.javarosa.core.model.instance.DataInstance
import org.javarosa.core.model.utils.DateUtils
import org.javarosa.xpath.parser.XPathSyntaxException

class XPathFormatDateFunc : XPathFuncExpr {

    constructor() {
        name = NAME
        expectedArgCount = EXPECTED_ARG_COUNT
    }

    @Throws(XPathSyntaxException::class)
    constructor(args: Array<XPathExpression>) : super(NAME, args, EXPECTED_ARG_COUNT, true)

    override fun evalBody(
        model: DataInstance<*>?,
        evalContext: EvaluationContext,
        evaluatedArgs: Array<Any>
    ): Any {
        return dateStr(evaluatedArgs[0], evaluatedArgs[1])
    }

    companion object {
        @JvmField
        val NAME = "format-date"
        private const val EXPECTED_ARG_COUNT = 2

        private fun dateStr(od: Any, of: Any): String {
            val expandedDate = FunctionUtils.expandDateSafe(od) ?: return ""
            return DateUtils.format(expandedDate, FunctionUtils.toString(of))
        }
    }
}
