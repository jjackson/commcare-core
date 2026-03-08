package org.javarosa.core.model.instance;

/**
 * Java bridge class that preserves raw-type getRoot() behavior.
 * In Java, ExternalDataInstance.getRoot() returned raw AbstractTreeElement
 * despite extending DataInstance<TreeElement>. Kotlin's type system prevents
 * this (it generates a checkcast to TreeElement), so this bridge class
 * handles the override in Java where raw types work.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class AbstractExternalDataInstance extends DataInstance<TreeElement> {

    public AbstractExternalDataInstance() {
        super();
    }

    public AbstractExternalDataInstance(String instanceId) {
        super(instanceId);
    }

    @Override
    public AbstractTreeElement getRoot() {
        return getRootElement();
    }

    protected abstract AbstractTreeElement getRootElement();
}
