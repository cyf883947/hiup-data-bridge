package com.djhu.service;

/**
 * @author cyf
 * @description     模拟单链表实现
 * @create 2020-04-29 9:40
 **/
public class SingleLinkedList {


    public static void main(String[] args) {

        LinkedList linkedList = new LinkedList();

        linkedList.addNode(new HeroNode("1","宋江","及时雨"));
        linkedList.addNode(new HeroNode("2","卢俊义","玉麒麟"));
        linkedList.addNode(new HeroNode("3","吴用","智多星"));

        linkedList.list();

    }




    static class LinkedList {
        // 设置一个头结点，不放数据
        private HeroNode head = new HeroNode("","","");

        // 添加结点数据，思路：将结点添加到最后一个节点的next 位置
        public void addNode(HeroNode node){
            /*
            1. 找到最后一个节点
            2. 将要添加的节点添加到最后一个位置
             */

            // 找最后一个节点，因为头节点不能动，所以设置一个辅助变量
            HeroNode temp = head;

            // 死循环，找最后一个节点
            while (true){
                // 当找到节点的next 指向空的时候，就是最后一个节点
                if(temp.next  == null){
                    break;
                }
                // 否则将节点后移一个位置
                temp = temp.next;
            }

            // 找到最后一个节点的位置，将当前节点设置到最后一个节点的next位置
            temp.next = node;
        }


        // 打印当前单链表的所有列表
        public void list(){
            // 空列表就不用打印了，判断列表是否为空，只要判断head 的next 节点是否指向空
            if(head.next == null){
                return;
            }

            HeroNode temp = head;

            // 否则开始没一个节点
            while (true){
                // 如果是最后节点，剩下的就不需要打印了，判读当前节点的next 是否指向为空即可
                if(temp.next == null){
                    break;
                }
                // 一定要后移一个位置，否则死循环，一直打打印头节点
                temp =  temp.next;
                System.out.println(temp);
            }
        }


    }


    static class HeroNode {
        private String no;
        private String name;
        private String nikeName;
        private HeroNode next;


        public HeroNode(String no, String name, String nikeName) {
            this.no = no;
            this.name = name;
            this.nikeName = nikeName;
        }

        public String getNo() {
            return no;
        }

        public void setNo(String no) {
            this.no = no;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNikeName() {
            return nikeName;
        }

        public void setNikeName(String nikeName) {
            this.nikeName = nikeName;
        }

        public HeroNode getNext() {
            return next;
        }

        public void setNext(HeroNode next) {
            this.next = next;
        }

        @Override
        public String toString() {
            return "HeroNode{" +
                    "no='" + no + '\'' +
                    ", name='" + name + '\'' +
                    ", nikeName='" + nikeName + '}';
        }
    }


}
