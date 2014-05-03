-- Add is_local_only to user. Used to indicate users which are not present on the server. Such users can only be used by the current app. This is useful for keeping track of favourites for anonymous users (the app created an anonymous user which is used when the user has not been signed on).

ALTER TABLE "user" ADD COLUMN is_local_only INTEGER NOT NULL DEFAULT 0;

-- Add status to favourite_activity. Indicated whether or not new favourites have been sent to the server.

-- Add server_side_favourite_count to activity. Number of users who have "favourited" the activity.
-- Add server_side_rating_count to activity. Number of users who have rated the activity.
-- Add server_side_rating_sum to activity. Total sum of all ratings of this activity. Used with server_side_rating_count to calculate average which considers current user's unsynchronized rating.
-- Add server_side_comment_count to activity. Number of server-side comments. Useful when number of comments should be shown to user without having to download them all (in case the user is not interested in reading them).