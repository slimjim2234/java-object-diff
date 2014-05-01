/*
 * Copyright 2014 Daniel Bechler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.danielbechler.diff.inclusion;

import de.danielbechler.diff.path.NodePath;

/**
 * Allows to in- or exclude nodes based on property name, object type, category or location in the object graph.
 * Excluded nodes will not be compared, to make sure their accessors won't get called. This is useful in cases where
 * getters could throw exceptions under certain conditions or when certain accessors are expensive to call or simply
 * not relevant for the use-case.
 * <p/>
 * In combination with categories this allows to define sub-sets of properties, in order to compare only relevant parts
 * of an object (e.g. exclude all properties marked as _metadata_.)
 *
 * @author Daniel Bechler
 */
public interface InclusionConfiguration<T>
{
	/**
	 * Includes elements (and implicitly all their children) based on certain criteria, unless their parent element
	 * is excluded.
	 */
	ToInclude<T> include();

	ToExclude<T> exclude();

	public interface ToInclude<T>
	{
		ToIncludeAndReturn<T> category(String category);

		ToIncludeAndReturn<T> type(Class<?> type);

		ToIncludeAndReturn<T> node(NodePath nodePath);

		ToIncludeAndReturn<T> propertyName(String propertyName);

		ToExclude<T> exclude();
	}

	public interface ToIncludeAndReturn<T> extends ToInclude<T>
	{
		T and();
	}

	public interface ToExclude<T>
	{
		ToExcludeAndReturn<T> category(String category);

		ToExcludeAndReturn<T> type(Class<?> type);

		ToExcludeAndReturn<T> node(NodePath nodePath);

		ToExcludeAndReturn<T> propertyName(String property);

		ToInclude<T> include();
	}

	public interface ToExcludeAndReturn<T> extends ToExclude<T>
	{
		T and();
	}
}
