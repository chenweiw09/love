create table `file_record` (
  `id` bigint not null auto_increment comment '主键ID',
  origin_file_name varchar(100) not null comment '原始文件名字',
   file_name varchar(100) not null comment '文件名字',
   file_size double default 0 comment '文件大小M',
   dir_path varchar(256) not null comment '文件保存路径',
   `desc` varchar(512) not null comment '文件描述',
   gmt_create timestamp null default null ,
   gmt_modified timestamp NULL DEFAULT NULL ON update CURRENT_TIMESTAMP,
    primary key (`id`)
) engine=InnoDB default charset=utf8mb4 COLLATE=utf8mb4_unicode_ci;


insert into file_record (origin_file_name, file_name, file_size, dir_path, `desc`, gmt_create, gmt_modified) values('1','2','3', '4', '5',  null, null );
