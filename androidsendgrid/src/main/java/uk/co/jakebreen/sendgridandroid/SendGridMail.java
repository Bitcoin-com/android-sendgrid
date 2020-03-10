package uk.co.jakebreen.sendgridandroid;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.co.jakebreen.sendgridandroid.FileEncoder.encodeFileToBase64;
import static uk.co.jakebreen.sendgridandroid.FileEncoder.getFileName;

public class SendGridMail {

    static final String EMPTY = "";
    static final String TYPE_PLAIN = "text/plain";
    static final String TYPE_HTML = "text/html";

    private final Map<String, String> to = new HashMap<>();
    private final Map<String, String> cc = new HashMap<>();
    private final Map<String, String> bcc = new HashMap<>();
    private String subject;
    private final Map<String, String> content = new HashMap<>();
    private final Map<String, String> from = new HashMap<>();
    private final Map<String, String> replyTo = new HashMap<>();
    private String templateId;
    private int sendAt;
    private List<Attachment> attachments = new ArrayList<>();

    public SendGridMail() { }

    /**
     * Add a new recipient up to a maximum of 1000 recipients.
     * Email address must be specified and an optional name of person or company
     * that is receiving this mail.
     *
     * @param email the recipient's email address
     * @param name  name of person or company that is receiving this mail
     */
    public void addRecipient(@NonNull String email, @Nullable String name) {
        if (to.size() >= 1000) return;

        if (name == null) name = EMPTY;
        to.put(email, name);
    }

    /**
     * Add a new Carbon Copy recipient up to a maximum of 1000 recipients.
     * Email address must be specified and an optional name of person or company
     * that is receiving this mail.
     *
     * @param email the recipient's email address
     * @param name  name of person or company that is receiving this mail
     */
    public void addRecipientCarbonCopy(@NonNull String email, @Nullable String name) {
        if (cc.size() >= 1000) return;

        if (name == null) name = EMPTY;
        cc.put(email, name);
    }

    /**
     * Add a new Blind Carbon Copy recipient up to a maximum of 1000 recipients.
     * Email address must be specified and an optional name of person or company
     * that is receiving this mail.
     *
     * @param email the recipient's email address
     * @param name  name of person or company that is receiving this mail
     */
    public void addRecipientBlindCarbonCopy(@NonNull String email, @Nullable String name) {
        if (bcc.size() >= 1000) return;

        if (name == null) name = EMPTY;
        bcc.put(email, name);
    }

    /**
     * Provide the senders email address and optional name or company that is
     * sending this mail.
     *
     * @param email email of person or company that is sending this mail
     * @param name  name of person or company that is sending this mail
     */
    public void setFrom(@NonNull String email, @Nullable String name) {
        if (name == null)
            name = EMPTY;
        from.put(email, name);
    }

    /**
     * The name of the person or company that is sending the email.
     *
     * @param email email of person or company that is sending this mail
     * @param name  name of person or company that is sending this mail
     */
    public void setReplyTo(@NonNull String email, @Nullable String name) {
        if (name == null)
            name = EMPTY;
        from.put(email, name);
    }

    /**
     * The subject matter of your email.
     *
     * @param subject subject of your email
     */
    public void setSubject(@NonNull String subject) {
        if (subject.length() == 0)
            subject = " ";
        this.subject = subject;
    }

    /**
     * The id of the template that this email should adhere to.
     *
     * @param templateId the id of your designated template
     */
    public void setTemplateId(@NonNull String templateId) {
        this.templateId = templateId;
    }

    /**
     * Add a plain text body to your email using Content-Type: "text/plain".
     *
     * @param body the body of your email
     */
    public void setContent(@NonNull String body) {
        if (body.length() == 0)
            body = " ";
        content.put(TYPE_PLAIN, body);
    }

    /**
     * Add a HTML body to your email using Content-Type: "text/html".
     *
     * @param body the body of your email
     */
    public void setHtmlContent(@NonNull String body) {
        if (body.length() == 0)
            body = " ";
        content.put(TYPE_HTML, body);
    }

    /**
     * A unix timestamp allowing you to specify when you want your email to be delivered.
     *
     * @param sendAt the unix timestamp of when your email should be sent
     */
    public void setSendAt(int sendAt) {
        if (sendAt > System.currentTimeMillis() / 1000L)
            this.sendAt = sendAt;
    }

    /**
     * Add an attachment of type {@link File} to the email, up to a maximum of 10.
     *
     * @param file the content to be attached
     */
    public void addAttachment(@NonNull File file) {
        if (attachments.size() >= 10)
            return;
        if (file.canRead() && file.exists() && file.isFile())
            attachments.add(new Attachment(file));
    }

    /**
     * Add an attachment of type {@link Uri} to the email, up to a maximum of 10.
     *
     * @param uri the content to be attached
     */
    public void addAttachment(@NonNull Context context, @NonNull Uri uri) {
        if (attachments.size() >= 10)
            return;
        attachments.add(new Attachment(context, uri));
    }

    /**
     * Returns a list of attached file names.
     *
     * @return list of file names
     */
    List<String> getAttachments() {
        final List<String> fileNames = new ArrayList<>();
        for (Attachment a : attachments) {
            fileNames.add(a.filename);
        }
        return fileNames;
    }

    Map<String, String> getRecipients() {
        return to;
    }

    Map<String, String> getRecipientCarbonCopies() {
        return cc;
    }

    Map<String, String> getRecipientBlindCarbonCopies() {
        return bcc;
    }

    String getSubject() {
        return subject;
    }

    Map<String, String> getContent() {
        return content;
    }

    Map<String, String> getFrom() {
        return from;
    }

    Map<String, String> getReplyTo() {
        return replyTo;
    }

    String getTemplateId() {
        return templateId;
    }

    int getSendAt() {
        return sendAt;
    }

    List<Attachment> getFileAttachments() {
        return attachments;
    }

    class Attachment {
        private final String content;
        private final String filename;

        Attachment(File file) {
            this.content = encodeFileToBase64(file);
            this.filename = file.getName();
        }

        Attachment(Context context, Uri uri) {
            this.content = encodeFileToBase64(context, uri);
            this.filename = getFileName(context, uri);
        }

        String getContent() {
            return content;
        }

        String getFilename() {
            return filename;
        }
    }
}
