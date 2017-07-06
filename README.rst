Question Set Generator
======================

It generates question sets for interviewers with specified difficulty.

Requirements
------------

Software packages
~~~~~~~~~~~~~~~~~

You need to have following packages installed:

- Java development kit >= 8
- Gradle
- MySQL server

Database
~~~~~~~~

    **Note:** Make sure the MySQL server is up and running and you know the MySQL root password.

You need to have a MySQL user ``lis`` with password ``lis`` and with access to edit the databases ``lis`` and ``lis_test``. To create these, run the following commands in MySQL prompt as MySQL root user:

.. code-block:: sql

    > CREATE DATABASE lis;
    > CREATE DATABASE lis_test;
    > CREATE USER lis IDENTIFIED BY `lis`;
    > GRANT ALL PRIVILEGES ON `lis%`.* TO lis;

The database schema is stored in ``db.sql`` file. To initialize the database just run the following command from the terminal::

    $ mysql --user=lis --password=lis lis < db.sql
    $ mysql --user=lis --password=lis lis_test < db.sql

Or you can directly use the following shell script::

    $ ./update_database.sh

To load the sample question dataset, you need to have **python3** installed along with **mysql-connecter==2.1.4** package.
Once you have these installed, you can load the dataset by::

    # python3 load_dataset.py

Usage
-----

To run local server::

    $ gradle run

And then visit http://0.0.0.0:4567 in the browser.

To run the test cases::

    $ gradle test

Developers
----------

- **M**\ anish Munikar
- **A**\ akash Shrestha
- **R**\ ojina Deuja
- **S**\ ushil Shakya
