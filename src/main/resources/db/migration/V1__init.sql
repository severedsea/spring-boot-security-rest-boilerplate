CREATE TABLE `user` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`username` VARCHAR(50) NOT NULL,
	`password` VARCHAR(255) NOT NULL,
	`created` DATETIME NOT NULL,
	`updated` DATETIME NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `UNIQUE KEY` (`username`)
);

CREATE TABLE `session_token` (
	`token` VARCHAR(255) NOT NULL,
	`user` BIGINT(20) NOT NULL DEFAULT '0',
	PRIMARY KEY (`token`)
);
