# add secure-file-priv="" under [mysqld] in my.ini

USE `stackoverflow17_06`;

# questions and answers
SELECT PostId, PostTypeId, COUNT(DISTINCT postHistory.Id) AS VersionCount
INTO OUTFILE 'PostId_VersionCount_SO_17-06.csv' 
CHARACTER SET utf8mb4
FIELDS TERMINATED BY ','
OPTIONALLY ENCLOSED BY '\"'
ESCAPED BY '\"'
LINES TERMINATED BY '\n'
FROM PostHistory postHistory INNER JOIN Posts posts
  ON postHistory.PostId = posts.Id
WHERE
  PostTypeId IN (1, 2)
  AND PostHistoryTypeId IN (2, 5, 8)
GROUP BY PostId, PostTypeId;
