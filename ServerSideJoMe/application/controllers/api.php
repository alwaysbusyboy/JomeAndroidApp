<?php
/** 
 * Web Service API for Donation mobile application.
 * Created by 
 * 		kim unil	2014/07/08
 * Do not copy or use this module without kim unil's approval.
 * Contact email 
 * 		dlfdkr19@gmail.com
 */ 
?>
<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');


class Api extends CI_Controller {

	/**
	 * Index Page for this controller.
	 *
	 * Maps to the following URL
	 * 		http://example.com/index.php/api
	 *	- or -  
	 * 		http://example.com/index.php/api/index
	 *	- or -
	 * Since this controller is set as the default controller in 
	 * config/routes.php, it's displayed at http://example.com/
	 *
	 * So any other public methods not prefixed with an underscore will
	 * map to /index.php/welcome/<method_name>
	 * @see http://codeigniter.com/user_guide/general/urls.html
	 * 
	 */
	
	function __construct() {
		parent::__construct();
	
		$this->load->helper('security');

        $this->load->model('user_model');
        $this->load->model('event_model');
	}
		
	// Response data
	private $response = array('error' => 0, 'message' => '', 'data'=> array());
	
	// Echo response data to client
	private function echo_response()
	{
		$this->output->set_content_type('application/json')->set_output(json_encode($this->response));
	}

	/***************************************************************************
	 *
	 * Web Service API
	 *
	 **************************************************************************/
	 
	// Default method
	public function index()
	{

	}

    // create_user
    public function create_user()
    {
        // Get parameter
        $firstname      = urldecode($this->input->get('firstname'));
        $lastname       = urldecode($this->input->get('lastname'));
        $email          = urldecode($this->input->get('email'));
        $facebook_id    = urldecode($this->input->get('facebook_id'));
        $interest       = urldecode($this->input->get('interest'));
        $profile_pic    = urldecode($this->input->get('profile_pic'));
        $gender         = urldecode($this->input->get('gender'));
        $birthday       = urldecode($this->input->get('birthday'));

        $about          = urldecode($this->input->get('about'));
        $hometown       = urldecode($this->input->get('hometown'));

        if ($facebook_id == '')
        {
            // Response
            $this->response['error'] = 1;
            $this->response['message'] = 'FB ID is invalid';
            $this->echo_response();
            return;
        }

        // login
        $user_info = $this->user_model->login($facebook_id);

        if ($user_info)
        {
            $this->response['message'] = "Login successful";
        }
        else
        {
            // register user
            $user_info = $this->user_model->register_user($firstname, $lastname, $email, $facebook_id, $interest, $profile_pic, $gender, $birthday, $about, $hometown);
            $this->response['message'] = "You have successfully registered.";
        }

        // Response
        $this->response['data'] = $user_info;
        $this->echo_response();
    }

    // get_user_info
    public function get_user_info()
    {
        // Get parameter
        $facebook_id    = urldecode($this->input->get('facebook_id'));

        // get user info
        $user_info = $this->user_model->get_user_info($facebook_id);

        if (! $user_info)
        {
            $this->response['error'] = 1;
            $this->response['message'] = "Can't find user with that facebook id.";
            $this->echo_response();
            return;
        }

        // Response
        $this->response['data'] = $user_info;
        $this->echo_response();
    }

    // set_location_user
    public function set_location_user()
    {
        // Get parameter
        $user_id    = urldecode($this->input->get('user_id'));
        $long       = urldecode($this->input->get('long'));
        $lati       = urldecode($this->input->get('lati'));

        // save location
        $this->user_model->save_location($user_id, $long, $lati);

        // Response
        $this->echo_response();
    }

    // get_user_nearby
    public function get_users_nearby()
    {
        // Get parameter
        $user_id    = urldecode($this->input->get('user_id'));
        $long       = urldecode($this->input->get('long'));
        $lati       = urldecode($this->input->get('lati'));
        $distance   = urldecode($this->input->get('distance'));

        // get users
        $user_list = $this->user_model->get_users_nearby($long, $lati, $distance);

        // Response
        $this->response['data'] = $user_list;
        $this->echo_response();
    }

    // create_event
    public function create_event()
    {
        // Get parameter
        $user_id        = urldecode($this->input->get('user_id'));
        $start          = urldecode($this->input->get('event_start_datetime'));
        $end            = urldecode($this->input->get('event_end_datetime'));
        $description    = urldecode($this->input->get('description'));
        $event_name     = urldecode($this->input->get('event_name'));
        $lati       = urldecode($this->input->get('lati'));
        $long      = urldecode($this->input->get('long'));

        $start = strtotime($start);
        $start = date('Y-m-d H:i:s',$start);
        $end = strtotime($end);
        $end = date('Y-m-d H:i:s',$end);

        if ($this->event_model->is_exist($event_name))
        {
            $this->response['error'] = 1;
            $this->response['message'] = "Event entry already exists.";
            $this->echo_response();
            return;
        }

        $this->event_model->register_event($user_id, $start, $end, $description, $event_name, $lati, $long);
        $this->echo_response();
    }

    // get_all_event
    public function get_all_event()
    {
        // Get parameter
        $user_id    = urldecode($this->input->get('user_id'));
        $lati       = urldecode($this->input->get('lati'));
        $long       = urldecode($this->input->get('long'));

        $event_list = $this->event_model->get_all_event($user_id, $lati, $long);

        // Response
        $this->response['data'] = $event_list;
        $this->echo_response();
    }

    // get_event_detail
    public function get_event_detail()
    {
        // Get parameter
        $event_id       = urldecode($this->input->get('event_id'));

        $event_detail = $this->event_model->get_event_detail($event_id);

        if (! $event_detail)
        {
            $this->response['error'] = 1;
            $this->response['message'] = "Event not fount.";
            $this->echo_response();
            return;
        }

        // Response
        $this->response['data'] = $event_detail;
        $this->echo_response();
    }

    // add_event_payment
    public function add_event_payment()
    {
        // Get parameter
        $transaction_id     = urldecode($this->input->get('transaction_id'));
        $user_id            = urldecode($this->input->get('user_id'));
        $payment_date       = urldecode($this->input->get('payment_date'));
    }
	
	// invite_by_distance_interest
	public function invite_by_distance_interest()
	{
		// Get parameter
        $lati       = urldecode($this->input->get('lati'));
        $long       = urldecode($this->input->get('long'));
		$interest   = urldecode($this->input->get('interest'));
		$distance   = urldecode($this->input->get('distance'));

		$interest_list = array();
		
		if ($interest != "")
			$interest_list = explode(",", $interest);

        $user_list = $this->user_model->get_by_distance_interest($lati, $long, $interest_list, $distance);
		
		// Response
        $this->response['data'] = $user_list;
        $this->echo_response();
	}
	
	// invite_all_fb_friends
	public function invite_all_fb_friends()
	{
		// Get parameter
        $fbid       = urldecode($this->input->get('fbid'));
	
		$fbid_list = explode(",", $fbid);
		$fbid = implode(",", $fbid_list);
	
		$user_list = $this->user_model->get_all_fb_friends($fbid);
		
		// Response
        $this->response['data'] = $user_list;
        $this->echo_response();
	}

    // remove_event
    public function remove_event()
    {
        // Get parameter
        $user_id        = urldecode($this->input->get('user_id'));
        $event_id       = urldecode($this->input->get('event_id'));

        $ret = $this->event_model->remove_event($event_id, $user_id);

        if (! $ret)
        {
            $this->response['error'] = 1;
            $this->response['message'] = "You can't delete the event.";
        }

        $this->echo_response();
    }

    // update_event
    public function update_event()
    {
        // Get parameter
        $user_id        = urldecode($this->input->get('user_id'));
        $event_id        = urldecode($this->input->get('event_id'));
        $start          = urldecode($this->input->get('event_start_datetime'));
        $end            = urldecode($this->input->get('event_end_datetime'));
        $description    = urldecode($this->input->get('description'));
        $event_name     = urldecode($this->input->get('event_name'));
        $lati       = urldecode($this->input->get('lati'));
        $long      = urldecode($this->input->get('long'));

        $start = strtotime($start);
        $start = date('Y-m-d H:i:s',$start);
        $end = strtotime($end);
        $end = date('Y-m-d H:i:s',$end);

        $ret = $this->event_model->update_event($user_id, $event_id, $start, $end, $description, $event_name, $lati, $long);

        if (! $ret)
        {
            $this->response['error'] = 1;
            $this->response['message'] = "You can't update the event.";
        }

        $this->echo_response();
    }

    // join_event
    public function join_event()
    {
        // Get parameter
        $user_id        = urldecode($this->input->get('user_id'));
        $event_id       = urldecode($this->input->get('event_id'));
        $join           = urldecode($this->input->get('join'));

        $ret = $this->event_model->join_event($user_id, $event_id, $join);

        if ($ret == -1)
        {
            $this->response['error'] = 1;
            $this->response['message'] = "You can't join the event.";
        }

        if ($ret == -2)
        {
            $this->response['error'] = 1;
            $this->response['message'] = "You are joined/leaved already.";
        }

        $this->echo_response();
    }

    // get_joined_user
    public function get_joined_user()
    {
        // Get parameter
        $event_id       = urldecode($this->input->get('event_id'));

        $user_list = $this->event_model->get_joined_user($event_id);

        // Response
        $this->response['data'] = $user_list;
        $this->echo_response();
    }

    // add_photo
    public function add_photo()
    {
        // Get parameter
        $user_id        = urldecode($this->input->post('user_id'));

        if (! $this->user_model->add_photo($user_id))
		{
			$this->response['error'] = 1;
            $this->response['message'] = "Can't save photo.";
		}
		
        // Response
        $this->echo_response();
    }
	
	// delete photo
	public function del_photo()
	{
		// Get parameter
        $user_id        = urldecode($this->input->get('user_id'));
		$photo_id       = urldecode($this->input->get('photo_id'));
		
		if (! $this->user_model->del_photo($user_id, $photo_id))
		{
			$this->response['error'] = 1;
            $this->response['message'] = "Can't delete photo.";
		}
		
        // Response
        $this->echo_response();
	}

    // set_profile
    public function set_profile()
    {
        // Get parameter
        $user_id        = urldecode($this->input->post('user_id'));
        $birthday       = urldecode($this->input->post('birthday'));
        $nickname       = urldecode($this->input->post('nickname'));
        $interest       = urldecode($this->input->post('interest'));
        $about          = urldecode($this->input->post('about'));
        $hometown       = urldecode($this->input->post('hometown'));

        $attr           = urldecode($this->input->post('attr'));

		if (strlen($attr) > 0)
			$attr_list = json_decode($attr, true);
		else
			$attr_list = array();

        $this->user_model->set_profile($user_id, $birthday, $nickname, $interest, $about, $hometown);
        $this->user_model->set_attribute($user_id, $attr_list);

        // Response
        $this->echo_response();
    }

    // set_attribute
    public function set_attribute()
    {
        // Get parameter
        $user_id        = urldecode($this->input->get('user_id'));
        $attr           = urldecode($this->input->get('attr'));

        $attr_list = json_decode($attr, true);

        $this->user_model->set_attribute($user_id, $attr_list);

        // Response
        $this->echo_response();
    }

    // get_user_data
    public function get_user_data()
    {
        // Get parameter
        $user_id        = urldecode($this->input->get('user_id'));

        $user_data = $this->user_model->get_user_data($user_id);

        // Response
        $this->response['data'] = $user_data;
        $this->echo_response();
    }	
}

/* End of file api.php */
/* Location: ./application/controllers/api.php */