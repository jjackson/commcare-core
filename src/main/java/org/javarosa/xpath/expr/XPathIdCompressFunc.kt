package org.javarosa.xpath.expr

import org.javarosa.core.model.condition.EvaluationContext
import org.javarosa.core.model.instance.DataInstance
import org.javarosa.core.util.CompressingIdGenerator
import org.javarosa.xpath.XPathException
import org.javarosa.xpath.parser.XPathSyntaxException

class XPathIdCompressFunc : XPathFuncExpr {

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
        val input = FunctionUtils.toInt(evaluatedArgs[0]).toLong()
        val growthDigits = FunctionUtils.toString(evaluatedArgs[1])
        val leadDigits = FunctionUtils.toString(evaluatedArgs[2])
        val bodyDigits = FunctionUtils.toString(evaluatedArgs[3])
        val fixedBodyLength = FunctionUtils.toInt(evaluatedArgs[4]).toInt()

        try {
            return CompressingIdGenerator.generateCompressedIdString(
                input, growthDigits, leadDigits, bodyDigits, fixedBodyLength
            )
        } catch (iae: IllegalArgumentException) {
            throw XPathException(iae.message)
        }
    }

    companion object {
        @JvmField
        val NAME = "id-compress"
        private const val EXPECTED_ARG_COUNT = 5
    }
}
