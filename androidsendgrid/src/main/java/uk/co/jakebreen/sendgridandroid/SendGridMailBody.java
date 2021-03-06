package uk.co.jakebreen.sendgridandroid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Map.Entry;

import uk.co.jakebreen.sendgridandroid.SendGridMail.Attachment;

import static uk.co.jakebreen.sendgridandroid.SendGridMail.EMPTY;
import static uk.co.jakebreen.sendgridandroid.SendGridMail.TYPE_HTML;
import static uk.co.jakebreen.sendgridandroid.SendGridMail.TYPE_PLAIN;

class SendGridMailBody {

    private static final String BODY_PERSONALISATIONS = "personalizations";
    private static final String BODY_TO = "to";
    private static final String BODY_CC = "cc";
    private static final String BODY_BCC = "bcc";
    private static final String BODY_FROM = "from";
    private static final String BODY_SUBJECT = "subject";
    private static final String BODY_CONTENT = "content";
    private static final String BODY_TEMPLATE_ID = "template_id";
    private static final String BODY_REPLY_TO = "reply_to";
    private static final String BODY_SEND_AT = "send_at";
    private static final String BODY_ATTACHMENTS = "attachments";
    private static final String BODY_TRACKING_SETTINGS = "tracking_settings";

    private static final String PARAMS_EMAIL = "email";
    private static final String PARAMS_NAME = "name";
    private static final String PARAMS_CONTENT_TYPE = "type";
    private static final String PARAMS_CONTENT_VALUE = "value";
    private static final String PARAMS_ATTACHMENT_CONTENT = "content";
    private static final String PARAMS_ATTACHMENT_FILENAME = "filename";

    private static final String TRACKING_SETTINGS_CLICK_TRACKING = "click_tracking";

    private final JSONObject body;

    private SendGridMailBody(JSONObject body) {
        this.body = body;
    }

    static SendGridMailBody create(SendGridMail mail) {
        return new SendGridMailBody(createMailBody(mail));
    }

    private static JSONObject createMailBody(SendGridMail mail) {
        final JSONObject parent = new JSONObject();
        try {
            JSONArray personalization = new JSONArray();
            personalization.put(getToParams(mail));
            if (!mail.getRecipientCarbonCopies().isEmpty())
                personalization.put(getCcParams(mail));
            if (!mail.getRecipientBlindCarbonCopies().isEmpty())
                personalization.put(getBccParams(mail));
            parent.put(BODY_PERSONALISATIONS, personalization);
            parent.put(BODY_FROM, getFromParams(mail));
            parent.put(BODY_SUBJECT, getSubjectParams(mail));
            parent.put(BODY_CONTENT, getContentParams(mail));
            if (mail.getTemplateId() != null)
                parent.put(BODY_TEMPLATE_ID, getTemplateId(mail));
            if (!mail.getReplyTo().isEmpty())
                parent.put(BODY_REPLY_TO, getReplyToParams(mail));
            if (mail.getSendAt() != 0)
                parent.put(BODY_SEND_AT, getSendAt(mail));
            if (mail.getAttachments().size() > 0)
                parent.put(BODY_ATTACHMENTS, getAttachments(mail));
            if (mail.getClickTracking().size() > 0) {
                JSONObject trackingSettings = new JSONObject();
                JSONObject clickTracking = getTrackingSettings(mail);
                trackingSettings.put(TRACKING_SETTINGS_CLICK_TRACKING, clickTracking);
                parent.put(BODY_TRACKING_SETTINGS, trackingSettings);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parent;
    }

    JSONObject getBody() {
        return body;
    }

    static JSONArray getContentParams(SendGridMail mail) throws JSONException {
        final JSONArray jsonArray = new JSONArray();
        Map<String, String> contentMap = mail.getContent();
        if (contentMap.containsKey(TYPE_PLAIN)) {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put(PARAMS_CONTENT_TYPE, TYPE_PLAIN);
            jsonObject.put(PARAMS_CONTENT_VALUE, contentMap.get(TYPE_PLAIN));
            jsonArray.put(jsonObject);
            contentMap.remove(TYPE_PLAIN);
        }

        if (contentMap.containsKey(TYPE_HTML)) {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put(PARAMS_CONTENT_TYPE, TYPE_HTML);
            jsonObject.put(PARAMS_CONTENT_VALUE, contentMap.get(TYPE_HTML));
            jsonArray.put(jsonObject);
            contentMap.remove(TYPE_HTML);
        }

        for (Entry<String, String> set : contentMap.entrySet()) {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put(PARAMS_CONTENT_TYPE, set.getKey());
            jsonObject.put(PARAMS_CONTENT_VALUE, set.getValue());
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    static String getSubjectParams(SendGridMail mail) {
        return mail.getSubject();
    }

    static String getTemplateId(SendGridMail mail) {
        return mail.getTemplateId();
    }

    static JSONObject getToParams(SendGridMail mail) throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(BODY_TO, setEmails(mail.getRecipients()));
        return jsonObject;
    }

    static JSONObject getCcParams(SendGridMail mail) throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(BODY_CC, setEmails(mail.getRecipientCarbonCopies()));
        return jsonObject;
    }

    static JSONObject getBccParams(SendGridMail mail) throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(BODY_BCC, setEmails(mail.getRecipientBlindCarbonCopies()));
        return jsonObject;
    }

    static JSONObject getFromParams(SendGridMail mail) throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        for (Entry<String, String> set : mail.getFrom().entrySet()) {
            jsonObject.put(PARAMS_EMAIL, set.getKey());
            jsonObject.put(PARAMS_NAME, set.getValue());
        }
        return jsonObject;
    }

    static JSONObject getReplyToParams(SendGridMail mail) throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        for (Entry<String, String> set : mail.getReplyTo().entrySet()) {
            jsonObject.put(PARAMS_EMAIL, set.getKey());
            jsonObject.put(PARAMS_NAME, set.getValue());
        }
        return jsonObject;
    }

    static int getSendAt(SendGridMail mail) {
        return mail.getSendAt();
    }

    static JSONArray getAttachments(SendGridMail mail) throws JSONException {
        final JSONArray jsonArray = new JSONArray();
        for (Attachment attachment : mail.getFileAttachments()) {
            if (attachment.getContent().isEmpty())
                continue;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(PARAMS_ATTACHMENT_CONTENT, attachment.getContent());
            jsonObject.put(PARAMS_ATTACHMENT_FILENAME, attachment.getFilename());
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    static JSONObject getTrackingSettings(SendGridMail mail) throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        for (Entry<String, Boolean> set : mail.getClickTracking().entrySet()) {
            jsonObject.put(set.getKey(), set.getValue());
        }
        return jsonObject;
    }

    private static JSONArray setEmails(Map<String, String> emailMap) throws JSONException {
        int count = 0;
        final JSONArray jsonArray = new JSONArray();
        for (Entry<String, String> set : emailMap.entrySet()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(PARAMS_EMAIL, set.getKey());
            jsonObject.put(PARAMS_NAME, set.getValue().equals(EMPTY) ? null : set.getValue());
            jsonArray.put(jsonObject);

            count ++;
            if (count >= 1000) break;
        }
        return jsonArray;
    }
}
