drop database if exists spark_pay;
create database spark_pay;

drop table if exists spark_pay.`order`;
create table if not exists spark_pay.`order` (
    `id`          bigint unsigned auto_increment comment '自增主键' primary key,
    `order_no`    varchar(128)                        null comment '订单号',
    `deleted`     bit(1)    default b'0'              not null comment '状态:0: 未删除 1: 已删除 (公共字段)',
    `create_time` timestamp default CURRENT_TIMESTAMP not null comment '创建时间 (公共字段)',
    `update_time` timestamp default CURRENT_TIMESTAMP not null comment '最后更新时间 (公共字段)'
) comment '订单表' engine = InnoDB
    charset = utf8mb4
    collate = utf8mb4_general_ci;

drop table if exists spark_pay.`payment_transaction`;
create table spark_pay.`payment_transaction` (
    `id`             bigint unsigned auto_increment comment '自增主键' primary key,
    `pay_no`         varchar(32)                         not null comment '交易编号',
    `pay_amount`     decimal(9, 2)                       not null comment '支付金额',
    `payment_status` tinyint(4) unsigned                 not null default 0 comment '支付状态: 0: 待支付, 1: 已经支付, 2: 支付超时, 3:支付失败',
    `order_id`       bigint unsigned                     not null comment '订单号码',
    `revision`       int(11)   default null comment '乐观锁',
    `deleted`        bit(1)    default b'0'              not null comment '状态:0: 未删除 1: 已删除 (公共字段)',
    `create_time`    timestamp default CURRENT_TIMESTAMP not null comment '创建时间 (公共字段)',
    `update_time`    timestamp default CURRENT_TIMESTAMP not null comment '最后更新时间 (公共字段)'
) comment '支付交易' engine = InnoDB
    charset = utf8mb4
    collate = utf8mb4_general_ci;

drop table if exists spark_pay.`payment_transaction_log`;


create table if not exists spark_pay.`payment_transaction_log` (
    `id`             bigint unsigned auto_increment comment '自增主键' primary key,
    `pay_id`         bigint unsigned                       not null comment '支付交易ID',
    `transaction_no` varchar(32) default null comment '第三方交易号',
    `synch_log`      text comment '同步回调日志',
    `async_log`      text comment '异步回调日志',
    `revision`       int(11)     default null comment '乐观锁',
    `deleted`        bit(1)      default b'0'              not null comment '状态:0: 未删除 1: 已删除 (公共字段)',
    `create_time`    timestamp   default CURRENT_TIMESTAMP not null comment '创建时间 (公共字段)',
    `update_time`    timestamp   default CURRENT_TIMESTAMP not null comment '最后更新时间 (公共字段)'
) comment '支付交易日志' engine = InnoDB
    charset = utf8mb4
    collate = utf8mb4_general_ci;

drop table if exists spark_pay.`business`;
create table spark_pay.`business` (
    `id`          bigint unsigned auto_increment comment '自增主键' primary key,
    `say`         text comment '其他业务操作',
    `deleted`     bit(1)    default b'0'              not null comment '状态:0: 未删除 1: 已删除 (公共字段)',
    `create_time` timestamp default CURRENT_TIMESTAMP not null comment '创建时间 (公共字段)',
    `update_time` timestamp default CURRENT_TIMESTAMP not null comment '最后更新时间 (公共字段)'
) comment '其他业务表' engine = InnoDB
    charset = utf8mb4
    collate = utf8mb4_general_ci;

