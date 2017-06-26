#!/bin/sh

mysql lis -ulis -plis < db.sql
mysql lis_test -ulis -plis < db.sql
