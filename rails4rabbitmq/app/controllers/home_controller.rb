require 'bunny'
require 'json'
require 'cpruntime/rabbitmqclient'
require 'yaml'

class HomeController < ApplicationController
  # The index action doesn't need to do anything
  include CPRuntime
  def index
     CPRuntime.refreshDedicatedServiceJsonDoc
    p "json-----------#{CPRuntime.service_json_doc}"
    @hosts = getHosts('rabbit')
    p @hosts
    config = YAML::load(File.read(Rails.root.to_s + '/config/runtime.yml'))
    p config
    $use_cloudpi_source = config['use_cloudpi_source']
    p $use_cloudpi_source
  end

  # Extracts the connection string for the rabbitmq service from the
  # service information provided by Cloud Foundry in an environment
  # variable.
  def self.amqp_url
    services = JSON.parse(ENV['VCAP_SERVICES'], :symbolize_names => true)
    srv=services.values[0][0][:credentials]
    srv.each {|s|
      #return "amqp://"+s[:username]+":"+s[:password]+"@"+s[:host].to_s()+":"+s[:port].to_s+"/"+s[:vhost]
      return {:host => s[:hostname], :port => s[:port], :pass => s[:password], :user=>s[:user],:vhost=>s[:vhost]}
    }
    #    url = services.values.map do |srvs|
    #      srvs.map do |srv|
    #	"amqp://"+srv[:credentials][:username]+":"+srv[:credentials][:password]+"@"+srv[:credentials][:host].to_s()+":"+srv[:credentials][:port].to_s+"/"+srv[:credentials][:vhost]
    #      end
    #    end.flatten!.first
  end

  # Opens a client connection to the RabbitMQ service, if one isn't
  # already open.  This is a class method because a new instance of
  # the controller class will be created upon each request.  But AMQP
  # connections can be long-lived, so we would like to re-use the
  # connection across many requests.
  def self.client(hostname)
    if $use_cloudpi_source
      c = CPRuntime::RabbitMQClient.new.create_from_svc(hostname)
      c.start
      @client = c
    else
      unless @client
        c = Bunny.new(amqp_url)
        c.start
        @client = c

        # We only want to accept one un-acked message
        @client.qos :prefetch_count => 1
      end
    end

    @client
  end

  # Return the "nameless exchange", pre-defined by AMQP as a means to
  # send messages to specific queues.  Again, we use a class method to
  # share this across requests.
  def self.nameless_exchange(hostname)
    @nameless_exchange ||= client(hostname).exchange('')
  end

  # Return a queue named "messages".  This will create the queue on
  # the server, if it did not already exist.  Again, we use a class
  # method to share this across requests.
  def self.messages_queue(hostname)
    @messages_queue ||= client(hostname).queue("messages")
  end

  # The action for our publish form.
  def publish
    # Send the message from the form's input box to the "messages"
    # queue, via the nameless exchange.  The name of the queue to
    # publish to is specified in the routing key.
    HomeController.nameless_exchange(params[:host]).publish params[:message],
    :content_type => "text/plain",
    :key => "messages"
    # Notify the user that we published.
    flash[:published] = true
    redirect_to home_index_path
  end

  def get
    flash[:got] = :queue_empty

    # Wait for a message from the queue
    HomeController.messages_queue(params[:host]).subscribe(:ack => true, :timeout => 10,
    :message_max => 1) do |msg|
      # Show the user what we got
      flash[:got] = msg[:payload]
    end

    redirect_to home_index_path
  end
end
