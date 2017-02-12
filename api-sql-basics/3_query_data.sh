#!/usr/bin/env bash

#######
# Depends on creating the tables in section 2
#######

impala-shell -i localhost -d default -q "SHOW TABLES"

# run simple analytical query on hdfs-backed table. This query finds the top 10 users by number of ratings and shows their min and max rating.
impala-shell -i localhost -d default -q "SELECT
    userid,
    minimum_rating,
    maximum_rating,
    total
FROM (
    SELECT
        userid AS user,
        MIN(rating) AS minimum_rating,
        MAX(rating) as maximum_rating
    FROM
        user_item_ratings
    GROUP BY
        userid
    ) AS minmax_ratings
JOIN (
    SELECT
        userid,
        count(*) AS total
    FROM
        user_item_ratings
    GROUP BY
        userid
    ) AS totalratings
ON
    minmax_ratings.user=totalratings.userid
ORDER BY
    total DESC
LIMIT 10"

# run simple analytical query on kudu-backed table.
impala-shell -i localhost -d default -q "SELECT
    userid,
    minimum_rating,
    maximum_rating,
    total
FROM (
    SELECT
        userid AS user,
        MIN(rating) AS minimum_rating,
        MAX(rating) as maximum_rating
    FROM
        kudu_user_ratings
    GROUP BY
        userid
    ) AS minmax_ratings
JOIN (
    SELECT
        userid,
        count(*) AS total
    FROM
        kudu_user_ratings
    GROUP BY
        userid
    ) AS totalratings
ON
    minmax_ratings.user=totalratings.userid
ORDER BY
    total DESC
LIMIT 10"

# Perform 'random read' query on HDFS data
impala-shell -i localhost -d default -q "SELECT COUNT(*), AVG(rating) FROM user_item_ratings WHERE movieid=470 AND userid=276"

# Perform 'random read' query on Kudu (Kudu table is partitioned and uses B-Tree index for fast random access)
impala-shell -i localhost -d default -q "SELECT COUNT(*), AVG(rating) FROM kudu_user_ratings WHERE movieid=470 AND userid=276"

# Update and deletes in HDFS will fail
impala-shell -i localhost -d default -q "SELECT AVG(rating) FROM user_item_ratings WHERE movietitle='Tombstone (1993)'"
impala-shell -i localhost -d default -q "UPDATE user_item_ratings SET rating=5 WHERE movietitle='Tombstone (1993)'"
impala-shell -i localhost -d default -q "DELETE FROM user_item_ratings WHERE movietitle='Tombstone (1993)'"

# Update and delete in Kudu
impala-shell -i localhost -d default -q "SELECT AVG(rating) FROM kudu_user_ratings WHERE movietitle='Tombstone (1993)'"
impala-shell -i localhost -d default -q "UPDATE kudu_user_ratings SET rating=5 WHERE movietitle='Tombstone (1993)'"
impala-shell -i localhost -d default -q "DELETE FROM kudu_user_ratings WHERE movietitle='Tombstone (1993)'"

# Verify deletion
impala-shell -i localhost -d default -q "SELECT * FROM kudu_user_ratings WHERE movietitle='Tombstone (1993)'"

