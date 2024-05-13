package com.transit.graphbased_v2.service.helper;

import com.transit.graphbased_v2.domain.graph.nodes.ObjectAttributeClazz;
import com.transit.graphbased_v2.performacelogging.LogExecutionTime;
import com.transit.graphbased_v2.transferobjects.AccessTransferComponent;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.transit.graphbased_v2.common.RightsConstants.*;

@Component
public class AccessTransferComponentHelper {
    @LogExecutionTime
    public AccessTransferComponent getAccessTransferComponent(ObjectAttributeClazz objectAttributeClazz, UUID objectClazzId, String objectEntityClazz, UUID identityId) {
        var result = new AccessTransferComponent();
        result.setObjectId(objectClazzId);
        result.setObjectEntityClazz(objectEntityClazz);
        result.setIdentityId(identityId);
        result.setReadProperties(StringListHelper.stringToSet(objectAttributeClazz.getProperties().get(READ_PROPERTIES)));
        result.setWriteProperties(StringListHelper.stringToSet(objectAttributeClazz.getProperties().get(WRITE_PROPERTIES)));
        result.setShareReadProperties(StringListHelper.stringToSet(objectAttributeClazz.getProperties().get(SHARE_READ_PROPERTIES)));
        result.setShareWriteProperties(StringListHelper.stringToSet(objectAttributeClazz.getProperties().get(SHARE_WRITE_PROPERTIES)));
        return result;
    }

}
