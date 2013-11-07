require 'sinatra'
require 'erb'
require 'rubygems'
require  'cpruntime/redisclient'
require 'json'
require 'yaml'

enable :sessions

class Hosts
  attr_accessor :hostname
end

class Post

  include CPRuntime
  def self.index
    CPRuntime.refreshDedicatedServiceJsonDoc
    p "json-----------#{CPRuntime.service_json_doc}"
    @hosts = getHosts('redis')
    p @hosts
  end

  # def self.redis_option
    # services = JSON.parse(ENV['VCAP_SERVICES'], :symbolize_names => true)
    # srv=services.values[0][0][:credentials]
    # srv.each {|s|
      # if s[:type] =~ /master/
        # return {:host => s[:hostname], :port => s[:port], :password => s[:password]}
      # end
    # }
  # end
# 
  # def self.redis(hostname)
    # if $use_cloudpi_source
      # @redis = CPRuntime::RedisClient.new.create_from_svc(hostname)
      # return @redis    
    # else
      # unless @redis
        # r = Redis.new(redis_option)
        #Redis.new({:host => "ec2-107-22-141-157.compute-1.amazonaws.com", :port => 6379, :password => "pvd1cKYtJ7eybyh46SNd"})
        # @redis = r
      # end
    # end
    # @redis
  # end
  
  def self.redis(hostname)
    @redis = CPRuntime::RedisClient.new.create_from_svc(hostname)
  end

end

  def take_session key
    res = session[key]
    session[key] = nil
    res
  end
include CPRuntime
  get '/' do
    CPRuntime.refreshDedicatedServiceJsonDoc
    # @env = ENV['VCAP_SERVICES']
    @store = take_session(:store)
    @got = take_session(:got)
    @hosts = getHosts('redis')  
    p @hosts
    erb :index
  end

  post '/set' do
    m=params[:message].split(":")
    Post.redis(params[:host]).set(m[0],m[1])
    # Post.redis("ec2-107-22-141-157.compute-1.amazonaws.com").set(m[0],m[1])
    session[:store] = true
    redirect to('/')
  end

  post '/get' do
    msg = Post.redis(params[:host]).get(params[:message])
    # msg = Post.redis("ec2-107-22-141-157.compute-1.amazonaws.com").get(params[:message])
    session[:got] = msg
    redirect to('/')
  end

