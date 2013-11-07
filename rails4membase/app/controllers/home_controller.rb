require 'memcache'
require 'json'
require  'cpruntime/memcacheclient'
require 'yaml'

class HomeController < ApplicationController
  include CPRuntime
  def index
    CPRuntime.refreshDedicatedServiceJsonDoc
    p "json-----------#{CPRuntime.service_json_doc}"
    @hosts = getHosts('membase')
    p @hosts
    config = YAML::load(File.read(Rails.root.to_s + '/config/runtime.yml'))
    p config
    $use_cloudpi_source = config['use_cloudpi_source']
    p $use_cloudpi_source
  end

  # Extracts the connection string for the membase service from the
  # service information provided by Cloud Foundry in an environment
  # variable.
  def self.membase_option
    services = JSON.parse(ENV['VCAP_SERVICES'], :symbolize_names => true)
    srv=services.values[0][0][:credentials]
    mem=[]
    srv.each {|s|
      mem.push(s[:host_ip].to_s()+ ":" + s[:port].to_s())
    }
    mem

  end

  # Opens a client connection to the membase service, if one isn't
  # already open.
  def self.membase(hostname)
    if $use_cloudpi_source
      c = CPRuntime::MemcacheClient.new.create_from_svc(hostname)
      @membase = c
    return @membase
    else
      unless @membase
        r = MemCache::new(membase_option[0])
        @membase = r
      end
    end

    @membase
  end

  # The action for our set form.
  def set
    m=params[:message].split(":")
    HomeController.membase(params[:host]).set(m[0],m[1])
    # Notify the user that we published.
    flash[:store] = true
    redirect_to home_index_path
  end

  def get
    # Synchronously get a message from the membase
    msg = HomeController.membase(params[:host]).get(params[:message])
    puts msg
    # Show the user what we got
    #flash[:got] = msg[:payload]
    flash[:got] = msg
    redirect_to home_index_path
  end
end
