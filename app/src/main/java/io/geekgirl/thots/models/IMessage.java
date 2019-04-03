package io.geekgirl.thots.models;

/**
 * Created by Rim Gazzah on 01/04/19
 */
public interface IMessage {
    String getId();
    String getUidSender();
    String getUidRecipient();
    String getMessage();
    User getUser();
}
