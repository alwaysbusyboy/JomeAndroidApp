<?php
/**
 * Created by PhpStorm.
 * User: wangming
 * Date: 7/13/14
 * Time: 10:03 PM
 */
?>
<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Event_model extends CI_Model {

    /*******************************************************************************************************************
     *
     * Model function
     *
     ******************************************************************************************************************/
    function __construct()
    {
        parent::__construct();
    }

   public function is_exist($event_name)
    {
        $sql = "SELECT * FROM event_details WHERE event_name = ?";
        $param_array = array($event_name);
        $query = $this->db->query($sql, $param_array);

        if ($query->num_rows() > 0)
            return true;
        else
            return false;
    }

    public function register_event($user_id, $start, $end, $description, $event_name, $latitude, $longitude)
    {
        $sql = "INSERT INTO event_details
                SET
                  user_id = ?, event_start_datetime = ?, event_end_datetime = ?, description = ?, event_name = ?, latitude = ?, longitude = ?";

        $param_array = array($user_id, $start, $end, $description, $event_name, $latitude, $longitude);
        $this->db->query($sql, $param_array);
    }

    public function get_all_event($user_id, $lati, $long)
    {
        $sql = "SELECT
                    event_details.*,
                    user_details.merchant,
                    (SELECT COUNT(*) FROM event_attend WHERE event_attend.event_id=event_details.id AND event_attend.user_id=? AND joined=1) AS joined
                FROM
                    event_details LEFT JOIN user_details ON event_details.user_id = user_details.fbid
                WHERE
                    public = 1 OR public = 0 AND
                    event_details.event_end_datetime >= UTC_TIMESTAMP() AND
                    event_details.deleted = 0
                ";
        $param_array = array($user_id);
        $query = $this->db->query($sql, $param_array);

        $event_list = array();

        foreach($query->result() as $event_item)
        {
            $event_list[] = array(
                "frds_invited"  => $event_item->frds_invited,
                "event_id"      => $event_item->id,
                "user_id"       => $event_item->user_id,
                "event_start"   => $event_item->event_start_datetime,
                "event_end"     => $event_item->event_end_datetime,
                "description"   => $event_item->description,
                "event_name"    => $event_item->event_name,
                "lati"          => $event_item->latitude,
                "long"          => $event_item->longitude,
                "distance"      => $this->distance($lati, $long, $event_item->latitude, $event_item->longitude),
                "merchant"      => $event_item->merchant,
                "joined"        => $event_item->joined
            );
        }

        return $event_list;
    }

    public function get_event_detail($event_id)
    {
        $sql = "SELECT
                    user_details.fname,
                    event_details.id,
                    event_details.description,
                    (
                        SELECT COUNT(*)
                        FROM event_attend, user_details
                        WHERE
                            event_attend.event_id=? AND
                            event_attend.user_id=user_details.id AND
                            event_attend.joined=1 AND
                            user_details.gender=1
                    ) AS joined_male,
                    (
                        SELECT COUNT(*)
                        FROM event_attend, user_details
                        WHERE
                            event_attend.event_id=? AND
                            event_attend.user_id=user_details.id AND
                            event_attend.joined=1 AND
                            user_details.gender=2
                    ) AS joined_female
                FROM
                    user_details INNER JOIN event_details ON user_details.id = event_details.user_id
                WHERE
                    event_details.id = ?";
        $param_array = array($event_id, $event_id, $event_id);
        $query = $this->db->query($sql, $param_array);

        if ($query->num_rows() > 0)
        {
            return array(
                "firstname"     => $query->row(0)->fname,
                "event_id"      => $query->row(0)->id,
                "description"   => $query->row(0)->description,
                "joined_male"   => $query->row(0)->joined_male,
                "joined_female" => $query->row(0)->joined_female
            );
        }
        else
            return null;
    }

    public function remove_event($event_id, $user_id)
    {
        // check user permission for event
        $sql = "SELECT * FROM event_details WHERE id=? AND user_id=?";
        $param_array = array($event_id, $user_id);
        $query = $this->db->query($sql, $param_array);

        if ($query->num_rows() == 0)
            return false;

        $sql = "UPDATE event_details SET deleted=1 WHERE id=?";
        $param_array = array($event_id);
        $this->db->query($sql, $param_array);

        if ($this->db->affected_rows() == 0)
            return false;

        return true;
    }

    public function update_event($user_id, $event_id, $start, $end, $description, $event_name, $latitude, $longitude)
    {
        // check user permission for event
        $sql = "SELECT * FROM event_details WHERE id=? AND user_id=?";
        $param_array = array($event_id, $user_id);
        $query = $this->db->query($sql, $param_array);

        if ($query->num_rows() == 0)
            return false;

        $sql = "UPDATE event_details
                SET
                    event_start_datetime = ?, event_end_datetime = ?, description = ?, event_name = ?, latitude = ?, longitude = ?
                WHERE
                    id = ?
                  ";

        $param_array = array($start, $end, $description, $event_name, $latitude, $longitude, $event_id);
        $this->db->query($sql, $param_array);

        return true;
    }

    public function join_event($user_id, $event_id, $join)
    {
        // check user who is not creator.
        $sql = "SELECT * FROM event_details WHERE id=? AND user_id != ?";
        $param_array = array($event_id, $user_id);
        $query = $this->db->query($sql, $param_array);

        if ($query->num_rows() == 0)
            return -1;

        $query->free_result();

        // check user who is already joined or leaved
        $sql = "SELECT * FROM event_attend WHERE event_id=? AND user_id=?";
        $param_array = array($event_id, $user_id);
        $query = $this->db->query($sql, $param_array);

        if ($query->num_rows() > 0)
		{
			// update
			$sql = "UPDATE event_attend SET joined=? WHERE event_id=? AND user_id=?";
			$param_array = array($join, $event_id, $user_id);
			$this->db->query($sql, $param_array);
		}
		else
		{
			// insert			
			$sql = "INSERT INTO event_attend SET event_id=?, user_id=?, joined=?";
			$param_array = array($event_id, $user_id, $join);
			$this->db->query($sql, $param_array);
		}    

        return 1;
    }

    public function get_joined_user($event_id)
    {
        $sql = "SELECT
                    user_details.id AS user_id,
                    user_details.fname AS firstname,
                    user_details.lname AS lastname,
                    user_details.gender,
                    user_details.profile_pic AS profile_image
                FROM event_attend, user_details
                WHERE
                    event_attend.event_id=? AND
                    event_attend.user_id=user_details.id AND
                    event_attend.joined=1
                ORDER BY user_details.gender ASC
                    ";
        $param_array = array($event_id);
        $query = $this->db->query($sql, $param_array);

        $user_list = array();
        foreach($query->result() as $user_item)
        {
            $user_list[] = array(
                "user_id"       => $user_item->user_id,
                "firstname"     => $user_item->firstname,
                "lastname"      => $user_item->lastname,
                "gender"        => $user_item->gender,
                "profile_image" => $user_item->profile_image
            );
        }

        return $user_list;
    }

    private function distance($lat1, $lon1, $lat2, $lon2)
    {
        $theta = $lon1 - $lon2;
        $dist = sin(deg2rad($lat1)) * sin(deg2rad($lat2)) +  cos(deg2rad($lat1)) * cos(deg2rad($lat2)) * cos(deg2rad($theta));
        $dist = acos($dist);
        $dist = rad2deg($dist);
        $dist = $dist * 60 * 1853.1;

        if($dist > 1000)
        {
            $dist =  ($dist/1000);
            return round($dist,2)." KM";
        }
        else
        {
            return round($dist,2)." M";
        }
    }
}
