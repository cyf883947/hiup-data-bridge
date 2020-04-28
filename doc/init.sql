-- Create table
create table TB_DB_PURCHASER
(
  ID         VARCHAR2(64) not null,
  NAME       VARCHAR2(128) not null,
  TYPE       VARCHAR2(64),
  CREATETIME DATE default sysdate,
  UPDATETIME DATE default sysdate,
  STATUS     VARCHAR2(2) not null,
  CUSTOM0    VARCHAR2(128),
  CUSTOM1    VARCHAR2(128),
  CUSTOM2    VARCHAR2(128)
)
tablespace ATS_ES
  pctfree 10
  pctused 40
  initrans 1
  maxtrans 255
  storage
  (
    initial 64
    next 8
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table
comment on table TB_DB_PURCHASER
  is '数据接收厂商表';
-- Add comments to the columns
comment on column TB_DB_PURCHASER.NAME
  is '厂商名称';
comment on column TB_DB_PURCHASER.TYPE
  is '厂商类型';
comment on column TB_DB_PURCHASER.STATUS
  is '厂商状态 - 是否启用  0-启用 1-禁用';
comment on column TB_DB_PURCHASER.CUSTOM0
  is '厂商地址';
comment on column TB_DB_PURCHASER.CUSTOM1
  is '备用字段2';
comment on column TB_DB_PURCHASER.CUSTOM2
  is '备用字段3';
-- Create/Recreate primary, unique and foreign key constraints
alter table TB_DB_PURCHASER
  add constraint PRIMARY_KEY primary key (ID)
  using index
  tablespace ATS_ES
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );












-- Create table
create table TB_DB_PURCHASER_RECORD
(
  ID                  VARCHAR2(64) not null,
  PURCHASER_ID        VARCHAR2(32),
  CONSUME             VARCHAR2(32),
  CREATEDATE          DATE default sysdate,
  UPDATEDATE          DATE default sysdate,
  STATUS              VARCHAR2(2),
  CUSTOM0             VARCHAR2(256),
  CUSTOM1             VARCHAR2(64),
  CUSTOM2             VARCHAR2(64),
  HIS_ID              VARCHAR2(64),
  HIS_VISIT_ID        VARCHAR2(64),
  HIS_DOMAIN_ID       VARCHAR2(64),
  HIS_VISIT_DOMAIN_ID VARCHAR2(64),
  DB_ID               VARCHAR2(64),
  DB_NAME             VARCHAR2(64)
)
tablespace ATS_ES
  pctfree 10
  pctused 40
  initrans 1
  maxtrans 255
  storage
  (
    initial 64
    next 8
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table
comment on table TB_DB_PURCHASER_RECORD
  is '数据接收厂商推送记录表';
-- Add comments to the columns
comment on column TB_DB_PURCHASER_RECORD.PURCHASER_ID
  is '厂商id, 与表 tb_db_purchaser 的id关联';
comment on column TB_DB_PURCHASER_RECORD.CONSUME
  is '耗时';
comment on column TB_DB_PURCHASER_RECORD.STATUS
  is '推送状态 0-成功 1-失败';
comment on column TB_DB_PURCHASER_RECORD.DB_ID
  is '专科库id';
comment on column TB_DB_PURCHASER_RECORD.DB_NAME
  is '专科库名称';
-- Create/Recreate primary, unique and foreign key constraints
alter table TB_DB_PURCHASER_RECORD
  add constraint RECORD_PRIMARY_KEY primary key (ID)
  using index
  tablespace ATS_ES
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
