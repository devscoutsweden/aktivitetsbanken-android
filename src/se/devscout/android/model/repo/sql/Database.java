package se.devscout.android.model.repo.sql;

public class Database {

    public static class user {
        final static String T = "user";

        final static String id = "id";
        final static String server_id = "server_id";
        final static String server_revision_id = "server_revision_id";
        final static String api_key = "api_key";
        final static String display_name = "display_name";
    }

    public static class category {
        final static String T = "category";

        final static String id = "id";
        final static String server_id = "server_id";
        final static String uuid = "uuid";
        final static String group_name = "group_name";
        final static String name = "name";
        final static String owner_id = "owner_id";
        final static String status = "status";
        final static String server_revision_id = "server_revision_id";
    }

    public static class media {
        final static String T = "media";
        final static String id = "id";
        //        final static String data = "data";
        final static String server_id = "server_id";
        final static String uri = "uri";
        //        final static String status = "status";
        final static String is_publishable = "is_publishable";
        final static String mime_type = "mime_type";
        final static String server_revision_id = "server_revision_id";
//        final static String hash = "hash";
    }

    public static class reference {
        final static String T = "reference";

        final static String id = "id";
        final static String server_id = "server_id";
        final static String uri = "uri";
        final static String type = "type";
        final static String server_revision_id = "server_revision_id";
    }

    public static class activity {
        final static String T = "activity";
        final static String id = "id";
        final static String owner_id = "owner_id";
        final static String server_id = "server_id";
        final static String server_revision_id = "server_revision_id";
        final static String is_publishable = "is_publishable";
        final static String name = "name";
        final static String datetime_published = "datetime_published";
        final static String datetime_created = "datetime_created";
        final static String descr_material = "descr_material";
        final static String descr_introduction = "descr_introduction";
        final static String descr_prepare = "descr_prepare";
        final static String descr_activity = "descr_activity";
        final static String descr_safety = "descr_safety";
        final static String descr_notes = "descr_notes";
        final static String age_min = "age_min";
        final static String age_max = "age_max";
        final static String participants_min = "participants_min";
        final static String participants_max = "participants_max";
        final static String time_min = "time_min";
        final static String time_max = "time_max";
        final static String featured = "featured";
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

        final static String id = "id";
        final static String server_id = "server_id";
        final static String activity_id = "activity_id";
        final static String datetime_created = "datetime_created";
        final static String rating = "rating";
        final static String source_uri = "source_uri";
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

    public static class history {
        final static String T = "history";

        final static String id = "id";
        final static String user_id = "user_id";
        final static String type = "type";
        final static String data = "data";
    }
}
