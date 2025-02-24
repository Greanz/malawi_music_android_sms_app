<?php
	date_default_timezone_set("Africa/Blantyre");
	class receiver{
	    var $host="localhost",$password="root",$user="root",$db="mmc";
	    protected $con;
	    function __construct(){
	        $key = '123456';
	        $password='654321';

	        if(@$_POST['key']!==$key)exit("Wrong API key");
            if(@$_POST['password']!==$password)exit("Wrong API password");

            $this->con = @mysqli_connect($this->host,$this->user,$this->password,$this->db);
            if(!$this->con) exit("Error no server connection");
        }

        private function c($v){
	        return mysqli_real_escape_string($this->con,$v);
        }

        function save($msg){
	      $json = json_decode($msg);
	      $insert = "INSERT INTO sms_in(sms_text,sender_number,sent_dt,received_dt,receiver,msgtype,operator,message_id)VALUES";
	      $update = "UPDATE messages SET sms_status = '2' WHERE ";
	      $values=array();
	      $where=array();
	      foreach ($json as $key => $obj){
	          /*
	           * msg,From,Time,Id,Status,Seen,SyncTime
	           */
	          $operator = "NULL";
	          $null = NULL;
	          $values[] = ("
	            ('".$this->c($obj->msg)."',
	            '".$this->c($obj->From)."',
	            '".$this->c($obj->Time)."',
	            '".date("Y-m-d h:i:s")."',
	            '".$null."',
	            '".$null."',
	            '".$operator."',
	            '".$this->c($obj->Id)."')
              ");
	          $where[] = "id='$obj->Id'";
          }
	      $insert = $insert.implode(',',$values);
          mysqli_query($this->con,"DELETE FROM sms_in");
	      if(mysqli_query($this->con,$insert)) {
              return "Messages received";
          }
	      return "Unable to save messages";
        }
    }
    $receiver = new receiver();
	exit($receiver->save($_POST['message']));