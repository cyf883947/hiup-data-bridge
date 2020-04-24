# 数据推送和查询服务



逻辑整理    

推送接口    业务逻辑

专科库数据推送到多个厂商

--- 简单点


- 查询es中指定专科库数据
- 判断专科库数据是否与 patient_unique_id_bak 表记录是否一致，一致执行下一步，不一致，同步es数据到oracle表patient_unique_id_bak
- 判断厂商是否首次推送数据，
    - 首次，将全部数据直接推送到各个厂商。
    - 非首次，根据状态为2的数据与状态为空数据进行比较，用4个id进行比较。
        -  用状态为2的数据与状态为1的数据比较
            - 1.查询到的 ， 记录更新状态
            - 2.查询不到的，记录新增状态
            - 3.上次dbId数据总量 - 更新状态的数据总量 = 记录删除状态
            
            - 两者交集为空，判断上一次是否推送成功，
                - 推送成功 - 上次推送数据状态记录为删除、本次推送数据记录为新增，执行下一步
                - 推送失败 - 和上上次数据进行比较，参考上边1,2,3
                
- 执行推送  （通过Restful推送）

- 推送成功后，记录推送状态，
    更新推送厂商的名称，建一个表维护厂商字段。
    删除数据状态为1的的记录（oracle）
- 推送事变，记录推送状态，是个问题，怎么比较？？？？

查询接口  业务逻辑
- 根据推送的dbId 和 4个id进行查询，查询专科库，查询不到，返回患者不存在。

-- 要做的事情有哪些？

1. 生成患者编号代码修改，之前逻辑是存在的不处理，不存在的保存。                   -- 未做
    改成 先根据dbId 查询数据，有数据 - 记录更新状态。 无数据 - 保存当前库数据 ，记录新增状态。
    
2. research-web 删除数据库，先不清空Oracle。（待推送完了再删除，先查是否有状态为1的数据，有先清库，无则入库） -- 未做

3. 建两张表：
 第一张记录厂商推送记录信息表
 
  tb_db_Purchaser_record
 
 字段要有：

 id
 current_db_id      当前推送专科库数据的dbId
 Purchaser_id  推送的厂商 ,关联tb_db_Purchaser 表的id
 total       推送的数据总量
 external_db_id     对外提供查询的dbId
 es_index       当前专科库对应的index
 es_type        当前专科库对应的type
 consume        推送数据耗时，单位ms
 createDate     
 updateDate
 status      推送状态 0-成功 1-失败
 custom0
 custom1
 custom2
 
 
，第二张维护所有厂商表。
tb_db_Purchaser

id          
name        厂商名称
type        厂商类型
createTime
updateTime
status      是否启用 0-启用 1-禁用
custom0
custom1
custom2

4. oracle表patient_unique_id_bak 添加一个状态字段或者用 备用字段记录。status 1-已推 2-未推



