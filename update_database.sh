#!/bin/sh


echo "Updating database 'lis'..."
mysql lis -ulis -plis < db.sql
echo "Updating database 'lis_test'..."
mysql lis_test -ulis -plis < db.sql
echo "Done."
