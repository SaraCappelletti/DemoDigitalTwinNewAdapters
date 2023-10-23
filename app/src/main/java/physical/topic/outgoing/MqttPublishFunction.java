package physical.topic.outgoing;

import it.wldt.adapter.physical.event.PhysicalAssetActionWldtEvent;

import java.util.function.Function;

public interface MqttPublishFunction extends Function<PhysicalAssetActionWldtEvent<?>, String> {
}
