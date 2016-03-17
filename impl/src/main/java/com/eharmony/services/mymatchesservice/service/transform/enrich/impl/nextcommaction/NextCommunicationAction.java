package com.eharmony.services.mymatchesservice.service.transform.enrich.impl.nextcommaction;

import com.google.common.base.Objects;

/**
 * The next action a user should take to advance communication This includes the
 * action and the area the action applies to.
 * 
 * @author aricheimer
 *
 */
public class NextCommunicationAction {

    private CommunicationActionEnum action;

    private CommunicationActionAreaEnum area;

    /**
     * Get the action a user should take to advance communication
     * 
     * @return the action a user should take to advance communication (e.g.
     *         SEND, if the user needs to send a message)
     */
    public CommunicationActionEnum getAction() {

        return action;
    }

    /**
     * Get the area that the action is to be done in
     * 
     * @return the area that the action is to be done in, e.g.
     *         MUST_HAVE_CANT_STAND
     */
    public CommunicationActionAreaEnum getArea() {

        return area;
    }

    /**
     * Set the action a user should take to advance communication
     * 
     * @param action
     *            the action a user should take to advance communication (e.g.
     *            SEND, if the user needs to send a message)
     */
    public void setAction(CommunicationActionEnum action) {

        this.action = action;
    }

    /**
     * Set the area that the action is to be done in
     * 
     * @param area
     *            the area that the action is to be done in, e.g.
     *            MUST_HAVE_CANT_STAND
     */
    public void setArea(CommunicationActionAreaEnum area) {

        this.area = area;
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(getAction(), getArea());
    }

    @Override
    public boolean equals(Object object) {

        if (object instanceof NextCommunicationAction) {
            NextCommunicationAction that = (NextCommunicationAction) object;
            return Objects.equal(this.getAction(), that.getAction())
                    && Objects.equal(this.getArea(), that.getArea());
        }
        return false;
    }

    @Override
    public String toString() {

        return Objects.toStringHelper(this).add("action", action)
                .add("area", area).toString();
    }

}
