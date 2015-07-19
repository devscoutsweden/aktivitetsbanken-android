package se.devscout.android.model.repo.sql;

public class Database {

    public static class user {
        final static String T = "user";

        final static String id = "id";
        final static String server_id = "server_id";
        final static String server_revision_id = "server_revision_id";
        final static String api_key = "api_key";
        final static String display_name = "display_name";
        final static String email_address = "email_address";
    }

    public static class category {
        public final static String T = "category";

        public final static String id = "id";
        public final static String server_id = "server_id";
        public final static String uuid = "uuid";
        public final static String group_name = "group_name";
        public final static String name = "name";
        public final static String owner_id = "owner_id";
        public final static String status = "status";
        public final static String server_revision_id = "server_revision_id";
        public final static String icon_media_id = "icon_media_id";
        public final static String activities_count = "activities_count";
    }

    public static class media {
        public final static String T = "media";

        public final static String id = "id";
        public final static String server_id = "server_id";
        public final static String uri = "uri";
        public final static String is_publishable = "is_publishable";
        public final static String mime_type = "mime_type";
        public final static String server_revision_id = "server_revision_id";
    }

    public static class reference {
        public final static String T = "reference";

        public final static String id = "id";
        public final static String server_id = "server_id";
        public final static String uri = "uri";
        public final static String description = "description";
        public final static String server_revision_id = "server_revision_id";
    }

    public static class activity {
        public final static String T = "activity";

        public final static String id = "id";
        public final static String owner_id = "owner_id";
        public final static String server_id = "server_id";
        public final static String server_revision_id = "server_revision_id";
        public final static String is_publishable = "is_publishable";
        public final static String name = "name";
        public final static String datetime_published = "datetime_published";
        public final static String datetime_created = "datetime_created";
        public final static String descr_material = "descr_material";
        public final static String descr_introduction = "descr_introduction";
        public final static String descr_prepare = "descr_prepare";
        public final static String descr_activity = "descr_activity";
        public final static String descr_safety = "descr_safety";
        public final static String descr_notes = "descr_notes";
        public final static String age_min = "age_min";
        public final static String age_max = "age_max";
        public final static String participants_min = "participants_min";
        public final static String participants_max = "participants_max";
        public final static String time_min = "time_min";
        public final static String time_max = "time_max";
        public final static String featured = "featured";
        public final static String favourite_count = "favourite_count";
        public final static String rating_average = "rating_average";
    }

/*
    public static class activity_data {
        final static String T = "activity_data";
        final static String id = "id";
        final static String activity_id = "activity_id";
        final static String status = "status";
        final static String author_id = "author_id";
        final static String source_uri = "source_uri";
    }
*/

    public static class comment {
        final static String T = "comment";
        final static String id = "id";
        final static String server_id = "server_id";
        final static String server_revision_id = "server_revision_id";
        final static String is_publishable = "is_publishable";
        final static String activity_id = "activity_id";
        final static String status = "status";
        final static String text = "text";
        final static String datetime_created = "datetime_created";
        final static String owner_id = "owner_id";
    }

/*
    public static class comment_data {
        final static String T = "comment_data";
        final static String id = "id";
        final static String status = "status";
        final static String source_uri = "source_uri";
    }
*/

    public static class favourite_activity {
        final static String T = "favourite_activity";
        final static String activity_id = "activity_id";
        final static String user_id = "user_id";
    }

    public static class rating {
        final static String T = "rating";

        final static String activity_id = "activity_id";
        final static String rating = "rating";
        final static String status = "status";
        final static String user_id = "user_id";
    }

    public static class comment_media_items {
        final static String T = "comment_media_items";

        final static String comment_data_id = "comment_id";
        final static String media_id = "media_id";
    }

    public static class activity_data_media {
        final static String T = "activity_media_items";

        final static String activity_data_id = "activity_id";
        final static String media_id = "media_id";
        final static String featured = "featured";
    }

    public static class activity_data_reference {
        final static String T = "activity_references";
        final static String activity_data_id = "activity_id";
        final static String reference_id = "reference_id";
    }

    public static class activity_data_category {
        final static String T = "activity_categories";

        final static String activity_data_id = "activity_id";
        final static String category_id = "category_id";
    }

    public static class activity_relations {
        final static String T = "activity_relations";

        final static String activity_id = "activity_id";
        final static String related_activity_id = "related_activity_id";
    }

    public static class history {
        final static String T = "history";

        final static String id = "id";
        final static String user_id = "user_id";
        final static String type = "type";
        final static String data = "data";
    }

    public static class system_messages {
        final static String T = "system_messages";

        final static String id = "id";
        final static String server_id = "server_id";
        final static String server_revision_id = "server_revision_id";
        final static String valid_from = "valid_from";
        final static String valid_to = "valid_to";
        final static String key = "key";
        final static String value = "value";
    }
}
