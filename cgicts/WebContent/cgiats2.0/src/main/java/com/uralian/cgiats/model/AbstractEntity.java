/*
 * DomainObject.java Feb 5, 2008
 * 
 * Copyright 2009 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.model;

import java.io.Serializable;

import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import com.uralian.cgiats.util.Utils;

/**
 * This is the base class for all domain objects. The logic for checking the
 * object identity (used by {@link #hashCode()} and {@link #equals(Object)}
 * methods) is as follows:
 * 
 * <ul>
 * <li>{@link #hashCode()} always returns the value based on the
 * {@link #getBusinessKey()}, so that the object hash is consistent regardless
 * whether the object has received an id.</li>
 * <li>{@link #equals(Object)} method first checks if both objects have valid
 * IDs (values returned by {@link #getId()} method). If they both are not
 * <code>null</code>, and - in case they are numeric - their values do not
 * evaluate to <code>zero</code>, they are used for the equality check.
 * Otherwise, the values returned by {@link #getBusinessKey()} method are used
 * for comparison.</li>
 * </ul>
 * 
 * @author Vlad Orzhekhovskiy
 */
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractEntity<ID extends Serializable> implements
    Identifiable<ID>, Serializable
{
	private static final long serialVersionUID = -5669574706419182666L;

	/**
	 * Returns the id of the current object. If it is <code>null</code> or
	 * evaluates to 0, that means the object does not have a valid id yet.
	 * 
	 * @return the object id.
	 */
	public abstract ID getId();

	/**
	 * Returns the object business key. Business key encapsulates all object
	 * attributes that form the business key. It is used for identity check, if
	 * the {@link #getId()} does not return a valid identifier.
	 * 
	 * @return object business key.
	 */
	protected abstract Object getBusinessKey();

	/**
	 * Returns the hash code based on the object business key.
	 * 
	 * @return the hash code of the business key value.
	 * @see #getBusinessKey()
	 */
	@Override
	public int hashCode()
	{
		return Utils.safeHashCode(getBusinessKey());
	}

	/**
	 * Compares two objects based on their identities (the argument should be an
	 * instance of {@link AbstractEntity} class, otherwise the method will always
	 * return <code>false</code>).
	 * 
	 * If <b>both</b> objects have valid IDs - i.e. they are not <code>null</code>
	 * , and - if they are of {@link Number} type - evaluate to non-zero value,
	 * the IDs are tested for being equal. Otherwise, the method uses the values
	 * returned by {@link #getBusinessKey()} methods for comparison.
	 * 
	 * @param obj the object to compare with the current one.
	 * @return <code>true</code> if both objects are of the same type and have
	 *         equal identities, <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;

		if (obj == null || !(obj instanceof AbstractEntity<?>))
			return false;

		AbstractEntity<?> that = (AbstractEntity<?>) obj;

		Object thisId = this.getId();
		Object thatId = that.getId();

		if ((thisId == null) || (thatId == null)
		    || (thisId instanceof Number && ((Number) thisId).intValue() == 0)
		    || (thatId instanceof Number && ((Number) thatId).intValue() == 0))
		{
			Object thisBK = this.getBusinessKey();
			Object thatBK = that.getBusinessKey();
			return Utils.safeEqualsCheck(thisBK, thatBK);
		}
		else
			return Utils.safeEqualsCheck(thisId, thatId);
	}

	/**
	 * Utility method safely returning the entity's id or <code>null</code> if the
	 * entity is <code>null</code>.
	 * 
	 * @param entity identifiable entity.
	 * @return entity id or <code>null</code>.
	 */
	public static <T extends Serializable> T safeId(Identifiable<T> entity)
	{
		return entity != null ? entity.getId() : null;
	}
}