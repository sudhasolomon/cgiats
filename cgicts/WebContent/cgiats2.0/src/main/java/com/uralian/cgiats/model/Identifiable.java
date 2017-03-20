/*
 * Identifiable.java Feb 6, 2008
 * 
 * Copyright 2009 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.model;

import java.io.Serializable;

/**
 * This interface must be implemented by all domain objects that can be
 * identified by {@link #getId()} method.
 * 
 * @author Vlad Orzhekhovskiy
 */
public interface Identifiable<ID extends Serializable>
{
  /**
   * Returns the id of the current object. If it is <code>null</code> or
   * evaluates to numeric 0, that means the object does not have a valid id yet.
   * 
   * @return the object id.
   */
  public ID getId();
}
