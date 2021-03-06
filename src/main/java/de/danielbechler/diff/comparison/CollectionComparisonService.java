/*
 * Copyright 2015 Daniel Bechler
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

package de.danielbechler.diff.comparison;

import de.danielbechler.diff.inclusion.ValueNode;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.path.NodePath;

public class CollectionComparisonService implements IdentityStrategyResolver
{
	private final ValueNode<IdentityStrategy> nodePathIdentityStrategies;
	private final TypePropertyIdentityStrategyResolver typePropertyIdentityStrategyResolver;
	private final ComparisonConfigurer comparisonConfigurer;

	public CollectionComparisonService(final ComparisonConfigurer comparisonConfigurer)
	{
		this.comparisonConfigurer = comparisonConfigurer;
		this.nodePathIdentityStrategies = new ValueNode<IdentityStrategy>();
		this.typePropertyIdentityStrategyResolver = new TypePropertyIdentityStrategyResolver();
	}

	public IdentityStrategy resolveIdentityStrategy(final DiffNode node)
	{
		IdentityStrategy identityStrategy = typePropertyIdentityStrategyResolver.resolve(node);
		if (identityStrategy != null)
		{
			return identityStrategy;
		}
		identityStrategy = nodePathIdentityStrategies.getNodeForPath(node.getPath()).getValue();
		if (identityStrategy != null)
		{
			return identityStrategy;
		}
		return EqualsIdentityStrategy.getInstance();
	}

	public ComparisonConfigurer.OfCollectionItems ofCollectionItems(final NodePath nodePath)
	{
		return new OfCollectionItemsByNodePath(nodePath);
	}

	public ComparisonConfigurer.OfCollectionItems ofCollectionItems(final Class<?> type, final String propertyName)
	{
		return new OfCollectionItemsByTypeProperty(type, propertyName);
	}

	private class OfCollectionItemsByNodePath implements ComparisonConfigurer.OfCollectionItems
	{
		private final NodePath nodePath;

		public OfCollectionItemsByNodePath(final NodePath nodePath)
		{
			this.nodePath = nodePath;
		}

		public ComparisonConfigurer toUse(final IdentityStrategy identityStrategy)
		{
			nodePathIdentityStrategies.getNodeForPath(nodePath).setValue(identityStrategy);
			return comparisonConfigurer;
		}
	}

	private class OfCollectionItemsByTypeProperty implements ComparisonConfigurer.OfCollectionItems
	{
		private final Class<?> type;
		private final String propertyName;

		public OfCollectionItemsByTypeProperty(final Class<?> type, final String propertyName)
		{
			this.type = type;
			this.propertyName = propertyName;
		}

		public ComparisonConfigurer toUse(final IdentityStrategy identityStrategy)
		{
			typePropertyIdentityStrategyResolver.setStrategy(identityStrategy, type, propertyName);
			return comparisonConfigurer;
		}
	}
}
