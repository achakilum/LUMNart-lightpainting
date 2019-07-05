package com.bluelithalo.lumnart.pattern;

public interface HasHideableComponents
{
    void hideAllComponents(boolean recursiveHide);
    void hideComponent(int componentIdx, boolean recursiveHide);
    void unhideAllComponents(boolean recursiveUnhide);
    void unhideComponent(int componentIdx, boolean recursiveUnhide);
    boolean isComponentHidden(int componentIdx);
}
