package org.javarosa.xpath.expr

import org.commcare.modern.util.Pair
import org.javarosa.core.model.condition.EvaluationContext
import org.javarosa.core.model.instance.DataInstance
import org.javarosa.core.util.DataUtil
import org.javarosa.xpath.XPathArityException
import org.javarosa.xpath.XPathException
import org.javarosa.xpath.XPathTypeMismatchException
import org.javarosa.xpath.parser.XPathSyntaxException

class XPathSortByFunc : XPathFuncExpr {

    constructor() {
        name = NAME
        expectedArgCount = EXPECTED_ARG_COUNT
    }

    @Throws(XPathSyntaxException::class)
    constructor(args: Array<XPathExpression>) : super(NAME, args, EXPECTED_ARG_COUNT, true)

    @Throws(XPathSyntaxException::class)
    override fun validateArgCount() {
        if (args.size < 2 || args.size > 3) {
            throw XPathArityException(name, "2 or 3 arguments", args.size)
        }
    }

    override fun evalBody(
        model: DataInstance<*>?, evalContext: EvaluationContext, evaluatedArgs: Array<Any>): Any {
        val sortedList = if (evaluatedArgs.size == 2) {
            sortListByOtherList(
                FunctionUtils.toString(evaluatedArgs[0]),
                FunctionUtils.toString(evaluatedArgs[1]),
                true
            )
        } else {
            sortListByOtherList(
                FunctionUtils.toString(evaluatedArgs[0]),
                FunctionUtils.toString(evaluatedArgs[1]),
                FunctionUtils.toBoolean(evaluatedArgs[2])
            )
        }
        if (sortedList.isEmpty()) {
            throw XPathException(String.format("Called sort-by() on empty list with args %s", evaluatedArgs))
        }
        return DataUtil.listToString(sortedList)
    }

    private fun sortListByOtherList(s1: String, s2: String, ascending: Boolean): List<String> {
        val targetListItems = DataUtil.stringToList(s1)
        val comparisonListItems = DataUtil.stringToList(s2)

        if (targetListItems.size != comparisonListItems.size) {
            throw XPathTypeMismatchException(
                "Length of lists passed to sort-by() must match, " +
                        "but received lists: $s1 and $s2"
            )
        }

        val pairsList = createComparisonToTargetPairings(comparisonListItems, targetListItems)

        pairsList.sortWith { pair1, pair2 ->
            val comparisonStringDifferential = pair1.first.compareTo(pair2.first)
            if (comparisonStringDifferential != 0) {
                (if (ascending) 1 else -1) * comparisonStringDifferential
            } else {
                (if (ascending) 1 else -1) * pair1.second.compareTo(pair2.second)
            }
        }

        return pairsList.map { it.second }
    }

    companion object {
        @JvmField
        val NAME = "sort-by"

        // since we accept 2-3 arguments
        private const val EXPECTED_ARG_COUNT = -1

        private fun createComparisonToTargetPairings(
            comparisonListItems: List<String>,
            targetListItems: List<String>
        ): MutableList<Pair<String, String>> {
            val pairings = mutableListOf<Pair<String, String>>()
            for (i in comparisonListItems.indices) {
                val comparisonString = comparisonListItems[i]
                val targetString = targetListItems[i]
                pairings.add(Pair(comparisonString, targetString))
            }
            return pairings
        }
    }
}
