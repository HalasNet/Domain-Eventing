/*
    Copyright 2012, Strategic Gains, Inc.

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */
package com.strategicgains.eventing.hazelcast;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hazelcast.core.ITopic;
import com.strategicgains.eventing.EventHandler;
import com.strategicgains.eventing.EventTransport;

/**
 * @author toddf
 * @since Oct 18, 2012
 */
public class HazelcastEventTransport
implements EventTransport
{
	private ITopic<Object> topic;
	private Map<EventHandler, String> subscriptions = new ConcurrentHashMap<EventHandler, String>();

	protected HazelcastEventTransport()
	{
		super();
	}

	public HazelcastEventTransport(ITopic<Object> topic)
	{
		this();
		setTopic(topic);
	}

	protected void setTopic(ITopic<Object> aTopic)
    {
		this.topic = aTopic;
    }

	@Override
	public void publish(Object event)
	{
		topic.publish(event);
	}

	@Override
	public void shutdown()
	{
		topic.destroy();
	}

	@Override
	public boolean subscribe(EventHandler handler)
	{
		String listenerId = topic.addMessageListener(new EventHandlerAdapter(handler));
		subscriptions.put(handler, listenerId);
		return true;
	}

	@Override
	public boolean unsubscribe(EventHandler handler)
	{
		String listenerId = subscriptions.get(handler);

		if (listenerId != null)
		{
			return topic.removeMessageListener(listenerId);
		}

		return false;
	}
}
