require 'mongo'
require 'json'
require 'cpruntime'
require 'cpruntime/mongoclient'
require 'yaml'

class HomeController < ApplicationController
  include CPRuntime
  def index
CPRuntime.refreshDedicatedServiceJsonDoc
    p "json-----------#{CPRuntime.service_json_doc}"
    @hosts = getHosts('mongo')
    p @hosts
    config = YAML::load(File.read(Rails.root.to_s + '/config/runtime.yml'))
    p config
    $use_cloudpi_source = config['use_cloudpi_source']
    p $use_cloudpi_source
  end

  # Extracts the connection string for the mongodb service from the
  # service information provided by Cloud Foundry in an environment
  # variable.
  def self.mongodb_option
    services = JSON.parse(ENV['VCAP_SERVICES'], :symbolize_names => true)
    srv=services.values[0][0][:credentials]
    mongo=[]
    srv.each {|s|
      mongo.push(s[:host])
      mongo.push(s[:port])
      mongo.push(s[:db])
      mongo.push(s[:username])
      mongo.push(s[:password])
    }
    mongo
    #    mongodb_conf = services.values.map do |srvs|
    #      srvs.map do |srv|
    #        if srv[:label] =~ /^mongodb-/
    #          srv[:credentials][:host].to_s()+":"+srv[:credentials][:port].to_s()+":"+srv[:credentials][:db]+":"+srv[:credentials][:username]+":"+srv[:credentials][:password]
    #        end
    #      end
    #    end.flatten!.first
  end

  # Opens a client connection to the mongodb service, if one isn't
  # already open.
  def self.mongodb(hostname)
    if $use_cloudpi_source
      db = CPRuntime::MongoClient.new.create_from_svc(hostname)
      @mongodb = db.collection("users")
      return @mongodb
    else
      unless @mongodb
        db = Mongo::Connection.new(mongodb_option[0],mongodb_option[1]).db(mongodb_option[2])
        auth = db.authenticate(mongodb_option[3],mongodb_option[4])
        #db = Mongo::Connection.new("175.41.152.55",25001).db("db")
        #auth = db.authenticate("ac259011-e3f8-48c6-8a03-6ca0b28a5f3c","6ef77c4b-9e09-497e-b0c2-4979bb13e520")
        coll = db.collection("users")
        @mongodb = coll
        return  @mongodb
      end
    end
  end

  # The action for our set form.
  def set
    p params[:host]
    m=params[:message].split(":")
    HomeController.mongodb(params[:host]).insert("name" => m[0],"age" => m[1], "country" => m[2] )
    # Notify the user that we published.
    flash[:store] = true
    redirect_to home_index_path
  end

  def get
    p params[:host]
    # Synchronously get a message from the mongodb
    msg = HomeController.mongodb(params[:host]).find("name" => params[:message]).to_a.to_s
    puts msg
    # Show the user what we got
    #flash[:got] = msg[:payload]
    flash[:got] = msg
    redirect_to home_index_path
  end
end
