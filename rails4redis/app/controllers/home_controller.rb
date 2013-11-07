require  'cpruntime/redisclient'
require 'json'
require 'yaml'

class HomeController < ApplicationController
  include CPRuntime
  def index
      CPRuntime.refreshDedicatedServiceJsonDoc
    p "json-----------#{CPRuntime.service_json_doc}"
    @hosts = getHosts('redis')
    p @hosts
    config = YAML::load(File.read(Rails.root.to_s + '/config/runtime.yml'))
    p config
    $use_cloudpi_source = config['use_cloudpi_source']
    p $use_cloudpi_source
  end

  # Extracts the connection string for the redis service from the
  # service information provided by Cloud Foundry in an environment
  # variable.
  def self.redis_option
    services = JSON.parse(ENV['VCAP_SERVICES'], :symbolize_names => true)
    srv=services.values[0][0][:credentials]
    srv.each {|s|
      if s[:type] =~ /master/
        return {:host => s[:hostname], :port => s[:port], :password => s[:password]}
      end
    }
    #    redis_conf = services.values.map do |srvs|
    #      srvs.map do |srv|
    #        if srv[:label] =~ /^redis-/
    #        {:host => srv[:credentials][:hostname], :port => srv[:credentials][:port], :password => srv[:credentials][:password]}
    #        end
    #      end
    #    end.flatten!.first
  end

  # Opens a client connection to the redis service, if one isn't
  # already open.
  def self.redis(hostname)
    if $use_cloudpi_source
      @redis = CPRuntime::RedisClient.new.create_from_svc(hostname)
      return @redis
    else
      unless @redis
        r = Redis.new(redis_option)
        @redis = r
      end
    end
    @redis
  end

  # The action for our set form.
  def set
    m=params[:message].split(":")
    HomeController.redis(params[:host]).set(m[0],m[1])
    # Notify the user that we published.
    flash[:store] = true
    redirect_to home_index_path
  end

  def get
    # Synchronously get a message from the redis
    msg = HomeController.redis(params[:host]).get(params[:message])
    puts msg
    # Show the user what we got
    #flash[:got] = msg[:payload]
    flash[:got] = msg
    redirect_to home_index_path
  end
end
