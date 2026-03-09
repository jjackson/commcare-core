package org.javarosa.xpath

class XPathUnsupportedException : XPathException {
    constructor()

    constructor(s: String?) : super("unsupported construct [$s]")
}
