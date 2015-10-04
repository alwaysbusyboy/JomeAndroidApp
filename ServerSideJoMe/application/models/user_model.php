<?php
/**
 * Created by PhpStorm.
 * User: wangming
 * Date: 7/13/14
 * Time: 10:03 PM
 */
?>
<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

    // Upload path
    define('UPLOADPATH_PHOTO',	        'uploads/photo/');
    define('UPLOADFIELD_PHOTO',         'media');

class User_model extends CI_Model {

    // Get unique id (22character?)
    private function uniqid2str()
    {
        return str_replace(".", "", uniqid("", true));
    }

    // Get voice url by photo rel path
    private function photo2url($photo)
    {
        return base_url(UPLOADPATH_PHOTO . $photo);
    }

    /*******************************************************************************************************************
     *
     * Model function
     *
     ******************************************************************************************************************/
    function __construct()
    {
        parent::__construct();
    }

    // login
    public function login($facebook_id)
    {
        $sql = "SELECT * FROM user_details WHERE fbid = ?";
        $param_array = array($facebook_id);
        $query = $this->db->query($sql, $param_array);

        if ($query->num_rows() == 0)
            return false;

        $user_info = array(
            "user_id"       => $query->row(0)->id,
            "firstname"     => $query->row(0)->fname,
            "lastname"      => $query->row(0)->lname,
            "email"         => $query->row(0)->username,
            "interest"      => $query->row(0)->interest,
            "profile_image" => $query->row(0)->profile_pic
        );

        return $user_info;
    }

    // register_user
    public function register_user($firstname, $lastname, $email, $facebook_id, $interest, $profile_pic, $gender, $birthday, $about, $hometown)
    {
        $sql = "INSERT INTO user_details SET fbid=?, fname=?, lname=?, username=?, interest=?, profile_pic=?, gender=?, birthday=?, about=?, hometown=?";
        $param_array = array($facebook_id, $firstname, $lastname, $email, $interest, $profile_pic, $gender, $birthday, $about, $hometown);
        $this->db->query($sql, $param_array);

        $user_info = $this->login($facebook_id);

        return $user_info;
    }

    // get_user_info
    public function get_user_info($facebook_id)
    {
        $sql = "SELECT * FROM user_details WHERE fbid = ?";
        $param_array = array($facebook_id);
        $query = $this->db->query($sql, $param_array);

        if ($query->num_rows() == 0)
            return false;

        $user_info = array(
            "user_id"       => $query->row(0)->id,
            "firstname"     => $query->row(0)->fname,
            "lastname"      => $query->row(0)->lname,
            "email"         => $query->row(0)->username,
            "interest"      => $query->row(0)->interest,
            "profile_image" => $query->row(0)->profile_pic
        );

        return $user_info;
    }

    // save_location
    public function save_location($user_id, $long, $lati)
    {
        // find user
        $sql = "SELECT * FROM user_details WHERE id = ?";
        $param_array = array($user_id);
        $query = $this->db->query($sql, $param_array);

        if ($query->num_rows() == 0)
            return false;

        $query->free_result();

        // save location
        $sql = "SELECT * FROM user_location WHERE user_id = ?";
        $param_array = array($user_id);
        $query = $this->db->query($sql, $param_array);

        if ($query->num_rows() > 0)
        {
            // update location
            $sql = "UPDATE user_location SET geo_long = ?, geo_lati = ?, loc_date = UTC_TIMESTAMP() WHERE user_id = ?";
            $param_array = array($long, $lati, $user_id);
            $this->db->query($sql, $param_array);
        }
        else
        {
            // insert location
            $sql = "INSERT INTO user_location SET user_id = ?, geo_long = ?, geo_lati = ?, loc_date = UTC_TIMESTAMP()";
            $param_array = array($user_id, $long, $lati);
            $this->db->query($sql, $param_array);
        }

        return true;
    }

    // get_users_nearby
    public function get_users_nearby($long, $lati, $distance)
    {
        $R = 6371;  // earth's mean radius, km

        // first-cut bounding box (in degrees)
        $max_lati = $lati + rad2deg($distance/$R);
        $min_lati = $lati - rad2deg($distance/$R);

        // compensate for degrees longitude getting smaller with increasing latitude
        $max_long = $long + rad2deg($distance/$R/cos(deg2rad($lati)));
        $min_long = $long - rad2deg($distance/$R/cos(deg2rad($lati)));

        $sql = "SELECT
                    *
                FROM
                    user_location, user_details
                WHERE
                    TIMESTAMPDIFF(MINUTE, user_location.loc_date, UTC_TIMESTAMP()) < 120 AND
                    user_location.user_id = user_details.id AND
                    (
                        user_location.geo_lati >= ? AND
                        user_location.geo_lati <= ? AND
                        user_location.geo_long >= ? AND
                        user_location.geo_long <= ?
                    )";
        $param_array = array($min_lati, $max_lati, $min_long, $max_long);
        $query = $this->db->query($sql, $param_array);

        $user_list = array();

        foreach($query->result() as $user_item)
        {
            $user_list[] = array(
                "user_id"       => $user_item->id,
                "firstname"     => $user_item->fname,
                "lastname"      => $user_item->lname,
                "email"         => $user_item->username,
                "interest"      => $user_item->interest,
                "distance"      => $this->distance_geo_points($lati, $long, $user_item->geo_lati, $user_item->geo_long),
                "profile_image" => $user_item->profile_pic,
                "long"          => $user_item->geo_long,
                "lati"          => $user_item->geo_lati
            );
        }

        return $user_list;
    }

	// get_by_distance_interest
	public function get_by_distance_interest($lati, $long, $interest_list, $distance)
	{
		$R = 6371;  // earth's mean radius, km

        // first-cut bounding box (in degrees)
        $max_lati = $lati + rad2deg($distance/$R);
        $min_lati = $lati - rad2deg($distance/$R);

        // compensate for degrees longitude getting smaller with increasing latitude
        $max_long = $long + rad2deg($distance/$R/cos(deg2rad($lati)));
        $min_long = $long - rad2deg($distance/$R/cos(deg2rad($lati)));
		
		$condition = array();
		foreach($interest_list as $interest)
		{
			$condition[] = " user_details.interest LIKE '%" . $interest . "%'";
		}
		
		$sql = "SELECT 
					event_details.venue, 
					event_details.user_id, 
					user_details.interest, 
					user_details.profile_pic 
				FROM event_details INNER JOIN user_details ON user_details.id = event_details.user_id 
				WHERE 
					(event_details.latitude>=? AND event_details.latitude<=?) AND 
					(event_details.longitude>=? AND event_details.longitude<=?)";

		if (count($condition) > 0)
			$sql .= " AND " . implode(" OR ", $condition);
			
		$param_array = array($min_lati, $max_lati, $min_long, $max_long);
        $query = $this->db->query($sql, $param_array);

        $user_list = array();

        foreach($query->result() as $user_item)
		{
			$user_list[] = array(
				"user_id" 		=> $user_item->user_id,
				"venue"			=> $user_item->venue,
				"event_start"	=> $user_item->event_start_datetime,
				"description"	=> $user_item->description,
				"event_name"	=> $user_item->event_name,
				"interest"		=> $user_item->interest,
				"profile_image"	=> $user_item->profile_pic
			);			
		}
		
		return $user_list;
	}
	
	// get_all_fb_friends
	public function get_all_fb_friends($fbid)
	{
		$sql = "SELECT username, profile_pic FROM user_details WHERE fbid IN(?) ";
		
		$param_array = array($fbid);
        $query = $this->db->query($sql, $param_array);
		
		$user_list = array();
		
		foreach($query->result() as $user_item)
		{
			$user_list[] = array(
				"username" 		=> $user_item->username,
				"profile_image"	=> $user_item->profile_pic
			);			
		}
		
		return $user_list;
	}

    // add_photo($user_id)
    public function add_photo($user_id)
    {
        // save photo file
        $new_photo_rel_path = $this->save_file(UPLOADFIELD_PHOTO, UPLOADPATH_PHOTO, $this->uniqid2str());

		if ($new_photo_rel_path !== false)
		{
			// log photo
			$sql = "INSERT INTO user_photo SET user_id=?, photo_file=?";
			$param_array = array($user_id, $new_photo_rel_path);
			$this->db->query($sql, $param_array);
		
			return true;
		}
        
		return false;
    }
	
	// del_photo
	public function del_photo($user_id, $photo_id)
	{
		// check owner
		$sql = "SELECT * FROM user_photo WHERE user_id=? AND id=?";
		$param_array = array($user_id, $photo_id);
		$query = $this->db->query($sql, $param_array);
		
		if ($query->num_rows() == 0)
			return false;
			
		$this->del_file(UPLOADPATH_PHOTO, $query->row()->photo_file);	
		
		$sql = "DELETE FROM user_photo WHERE user_id=? AND id=?";
		$param_array = array($user_id, $photo_id);
		$this->db->query($sql, $param_array);
		
		return true;
	}

    // set_profile
    public function set_profile($user_id, $birthday, $nickname, $interest, $about, $hometown)
    {
        $sql = "UPDATE user_details SET birthday=?, nickname=?, interest=?, about=?, hometown=? WHERE id=?";
        $param_array = array($birthday, $nickname, $interest, $about, $hometown, $user_id);
        $this->db->query($sql, $param_array);
    }

    // set_attribute
    public function set_attribute($user_id, $attr_list)
    {
        // remove old attr
        $sql = "DELETE FROM user_attribute WHERE user_id=?";
        $param_array = array($user_id);
        $this->db->query($sql, $param_array);

        foreach($attr_list as $attr_item)
        {
            $sql = "INSERT INTO user_attribute SET user_id=?, attr_name=?, attr_value=?";
            $param_array = array($user_id, $attr_item['key'], $attr_item['val']);
            $this->db->query($sql, $param_array);
        }
    }

    // get profile + attribute + photo
    public function get_user_data($user_id)
    {
        $sql = "SELECT id, fname, lname, nickname, username, interest, profile_pic, gender, DATE(birthday) AS birthday, about, hometown FROM user_details WHERE id=?";
        $param_array = array($user_id);
        $query = $this->db->query($sql, $param_array);

        if ($query->num_rows() == 0)
            return false;

        $profile = array(
            "user_id"       => $query->row()->id,
            "firstname"     => $query->row()->fname,
            "lastname"      => $query->row()->lname,
            "nickname"      => $query->row()->nickname,
            "email"         => $query->row()->username,
            "interest"      => $query->row()->interest,
            "profile_image" => $query->row()->profile_pic,
            "gender"        => $query->row()->gender,
            "birthday"      => $query->row()->birthday,
            "about"         => $query->row()->about,
            "hometown"      => $query->row()->hometown
        );

        $query->free_result();

        // photo
        $sql = "SELECT id, photo_file FROM user_photo WHERE user_id=?";
        $param_array = array($user_id);
        $query = $this->db->query($sql, $param_array);

        $photo_list = array();

        if ($query->num_rows() > 0)
        {
            foreach($query->result() as $photo_item)
            {
                $photo_list[] = array(
					"photo_id"		=> $photo_item->id,
					"photo_url"		=> $this->photo2url($photo_item->photo_file)
				);
            }
        }

        $query->free_result();

        // attribute
        $sql = "SELECT attr_name, attr_value FROM user_attribute WHERE user_id=?";
        $param_array = array($user_id);
        $query = $this->db->query($sql, $param_array);

        $attr_list = array();

        if ($query->num_rows() > 0)
        {
            foreach($query->result() as $attr_item)
            {
                $attr_list[] = array(
                    "key"   => $attr_item->attr_name,
                    "val"   => $attr_item->attr_value
                );
            }
        }

        return array(
            "profile"       => $profile,
            "photo_list"    => $photo_list,
            "attr_list"     => $attr_list
        );
    }

    /***************************************************************************
     *
     * Utility functions
     *
     **************************************************************************/
    private function distance_geo_points($lat1, $lng1, $lat2, $lng2)
    {
        $earth_radius = 6371;

        $d_lati = deg2rad($lat2-$lat1);
        $d_long = deg2rad($lng2-$lng1);

        $a = sin($d_lati/2) * sin($d_lati/2) +
            cos(deg2rad($lat1)) * cos(deg2rad($lat2)) *
            sin($d_long/2) * sin($d_long/2);
        $c = 2 * atan2(sqrt($a), sqrt(1-$a));
        $dist = $earth_radius * $c;

        return $dist;
    }

    // get_file_ext
    private function get_file_ext($upload_field)
    {
        if (!isset($_FILES[$upload_field]))
            return "";

        if ($_FILES[$upload_field]['name'] == '')
            return "";

        // Get file extension
        $path_part = explode(".", $_FILES[$upload_field]['name']);
        $file_ext = count($path_part) > 1 ? ('.' . end($path_part)) : '';

        return $file_ext;
    }

    private function save_file($upload_field, $upload_path, $file_name)
    {
        // Save avatar
        $config['upload_path'] = $upload_path;
        $config['allowed_types'] = '*';
        $config['file_name'] = $file_name . $this->get_file_ext($upload_field);
        $config['overwrite'] = FALSE;

        $this->load->library('upload', $config);

        if ($this->upload->do_upload($upload_field))
        {
            $upload_data = $this->upload->data();

            return $upload_data['file_name'];
        }
		
        return false;
    }

    private function del_file($upload_path, $rel_path)
    {
        $config['upload_path'] = $upload_path;
        $this->load->library('upload', $config);
        $this->upload->validate_upload_path();

        $file_path = $this->upload->upload_path . $rel_path;

        @unlink($file_path);
    }
}