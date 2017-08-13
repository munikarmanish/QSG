-- Ignore foreign keys
SET FOREIGN_KEY_CHECKS = 0;

-- User
DROP TABLE IF EXISTS `users` CASCADE;
CREATE TABLE `users` (
    -- fields
    `id` INTEGER  AUTO_INCREMENT  NOT NULL,
    `email` VARCHAR(100)  NOT NULL,
    `username` VARCHAR(100)  NOT NULL,
    `passwordHash` CHAR(44)  NOT NULL,
    `name` VARCHAR(100),
    -- role:
    --  0: examiner
    --  1: operator
    --  2: admin
    `role` SMALLINT  NOT NULL  DEFAULT 0,
    -- timestamps
    `createdAt` timestamp  NOT NULL  DEFAULT current_timestamp,
    `updatedAt` timestamp  NOT NULL  DEFAULT current_timestamp  ON UPDATE current_timestamp,
    -- constraints
    PRIMARY KEY (`id`),
    UNIQUE KEY (`email`),
    UNIQUE KEY (`username`)
);

-- Category
DROP TABLE IF EXISTS `categories` CASCADE;
CREATE TABLE `categories` (
    -- fields
    `id` INTEGER  AUTO_INCREMENT  NOT NULL,
    `name` VARCHAR(100)  NOT NULL,
    -- timestamps
    `createdAt` timestamp  NOT NULL  DEFAULT current_timestamp,
    `updatedAt` timestamp  NOT NULL  DEFAULT current_timestamp  ON UPDATE current_timestamp,
    -- constraints
    PRIMARY KEY (`id`)
);

-- Question
DROP TABLE IF EXISTS `questions` CASCADE;
CREATE TABLE `questions` (
    -- fields
    `id` INTEGER  AUTO_INCREMENT  NOT NULL,
    `userId` INTEGER,
    `categoryId` INTEGER,
    `text` VARCHAR(1024)  NOT NULL,
    -- difficulty:
    --   0 - Easy
    --   1 - Medium
    --   2 - Hard
    `difficulty` SMALLINT  NOT NULL  DEFAULT 0,
    -- timestamps
    `createdAt` timestamp  NOT NULL  DEFAULT current_timestamp,
    `updatedAt` timestamp  NOT NULL  DEFAULT current_timestamp  ON UPDATE current_timestamp,
    -- constraints
    PRIMARY KEY (`id`),
    FOREIGN KEY (`userId`)  REFERENCES `users`(`id`)  ON DELETE SET NULL,
    FOREIGN KEY (`categoryId`)  REFERENCES `categories`(`id`)  ON DELETE SET NULL
);

-- Answer
DROP TABLE IF EXISTS `answers` CASCADE;
CREATE TABLE `answers` (
    -- fields
    `id` INTEGER  AUTO_INCREMENT  NOT NULL,
    `questionId` INTEGER  NOT NULL,
    `text` VARCHAR(255)  NOT NULL,
    `isCorrect` BOOLEAN  NOT NULL  DEFAULT FALSE,
    -- timestamps
    `createdAt` timestamp  NOT NULL  DEFAULT current_timestamp,
    `updatedAt` timestamp  NOT NULL  DEFAULT current_timestamp  ON UPDATE current_timestamp,
    -- constraints
    PRIMARY KEY (`id`),
    FOREIGN KEY (`questionId`)  REFERENCES `questions`(`id`)  ON DELETE CASCADE
);


-- exams
DROP TABLE IF EXISTS `exams` CASCADE;
CREATE TABLE `exams` (
    -- fields
    `id` INTEGER  AUTO_INCREMENT  NOT NULL,
    `title` VARCHAR(255)  NOT NULL,
    `time` TIMESTAMP  NOT NULL  DEFAULT current_timestamp,   -- date & time of exam
    `duration` INTEGER  NOT NULL  DEFAULT 30, -- in minutes
    -- difficulty:
    --   0 - Easy
    --   1 - Medium
    --   2 - Hard
    `difficulty` SMALLINT  NOT NULL  DEFAULT 0,
    `userId` INTEGER,
    -- timestamps
    `createdAt` timestamp  NOT NULL  DEFAULT current_timestamp,
    `updatedAt` timestamp  NOT NULL  DEFAULT current_timestamp  ON UPDATE current_timestamp,
    -- constraints
    PRIMARY KEY (`id`),
    FOREIGN KEY (`userId`)  REFERENCES `users`(`id`)  ON DELETE SET NULL
);

-- Set
DROP TABLE IF EXISTS `sets` CASCADE;
CREATE TABLE `sets` (
    -- fields
    `id` INTEGER  AUTO_INCREMENT  NOT NULL,
    `examId` INTEGER,
    `setNumber` SMALLINT  NOT NULL  DEFAULT 1,
    -- timestamps
    `createdAt` timestamp  NOT NULL  DEFAULT current_timestamp,
    `updatedAt` timestamp  NOT NULL  DEFAULT current_timestamp  ON UPDATE current_timestamp,
    -- constraints
    PRIMARY KEY (`id`),
    FOREIGN KEY (`examId`) REFERENCES `exams`(`id`)  ON DELETE SET NULL,
    UNIQUE (`examId`, `setNumber`)
);

-- Set Question many-many relation
DROP TABLE IF EXISTS `sets_questions` CASCADE;
CREATE TABLE `sets_questions` (
    -- fields
    `id` INTEGER  AUTO_INCREMENT  NOT NULL,
    `setId` INTEGER  NOT NULL,
    `questionId` INTEGER  NOT NULL,
    `questionNumber` SMALLINT  NOT NULL,
    `correctIndex` SMALLINT  NOT NULL,
    -- timestamps
    `createdAt` timestamp  NOT NULL  DEFAULT current_timestamp,
    `updatedAt` timestamp  NOT NULL  DEFAULT current_timestamp  ON UPDATE current_timestamp,
    -- constraints
    PRIMARY KEY (`id`),
    FOREIGN KEY (`setId`)  REFERENCES `sets`(`id`)  ON DELETE CASCADE,
    FOREIGN KEY (`questionId`)  REFERENCES `questions`(`id`)  ON DELETE CASCADE,
    UNIQUE (`setId`, `questionId`)
);

-- Enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;
