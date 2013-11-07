require 'sinatra'
require 'erb'

#require 'bunny'
#require 'memcache'
require 'json'

#require 'cpruntime/rabbitmqclient'
#require 'cpruntime/memcacheclient'
require 'cpruntime/mongoclient'
require 'cpruntime'
require 'yaml'

enable :sessions

class Hosts
  attr_accessor :hostname
end

class Post
  
  include CPRuntime
  def self.index
#    @hosts = [];
#    #m =   credential_map('rabbit')
#    m =   credential_map('membase')
#    i=0
#    m.each{|k,v|
#      h = Hosts.new
#      h.hostname = k
#      @hosts[i] = h
#      i = i+1
#    }
    # p @hosts
    @hosts = getHosts('mongo')
  end

#def self.amqp_url
#  services = JSON.parse(ENV['VCAP_SERVICES'], :symbolize_names => true)
#  srv=services.values[0][0][:credentials]
#    srv.each {|s|
#      #return "amqp://"+s[:username]+":"+s[:password]+"@"+s[:host].to_s()+":"+s[:port].to_s+"/"+s[:vhost]
#      return {:host => s[:hostname], :port => s[:port], :pass => s[:password], :user=>s[:user],:vhost=>s[:vhost]}
#    }
#end

def self.client(hostname)
#  if $use_cloudpi_source
#      c = CPRuntime::RabbitMQClient.new.create_from_svc(hostname)
#      c.start
#      @client = c
#  else
#    unless $client
#      c = Bunny.new(amqp_url)
#      c.start
#      @client = c
#      @client.qos :prefetch_count => 1
#    end 
#  end

#  CloudpiRuntime.refreshDedicatedServiceJsonDoc
#  c = CPRuntime::MemcacheClient.new.create_from_svc(hostname)
  db = CPRuntime::MongoClient.new.create_from_svc(hostname)
  c = db.collection("users")
  @client = c
  @client
end

#def self.nameless_exchange(hostname)
#  @nameless_exchange ||= client(hostname).exchange('')
#end

#def self.messages_queue(hostname)
#  @messages_queue ||= client(hostname).queue("messages")
#end

end

def take_session key
  res = session[key]
  session[key] = nil
  res
end
  include CPRuntime
get '/' do
  CPRuntime.refreshDedicatedServiceJsonDoc
  @published = take_session(:published)
  @got = take_session(:got)
#  @env = ENV['VCAP_SERVICES']
#  @hosts = [];
#    # m =   credential_map('rabbit')
#    m =   credential_map('membase')
#    i=0
#    m.each{|k,v|
#      h = Hosts.new
#      h.hostname = k
 #     @hosts[i] = h
#      i = i+1
#      p h.hostname
#    }
#    p @hosts

  @hosts = getHosts('mongo')
  #session[:hosts] = @hosts
  #p "hosts list: #{@hosts}"
  erb :index
end

post '/publish' do
  #Post.nameless_exchange(params[:host]).publish params[:message], :content_type => "text/plain",
  #                          :key => "messages"

  Post.client(params[:host1]).insert("zwzkey" => params[:key1], "zwzval" => params[:message] )
#     .set params[:key1],params[:message]
  session[:published] = true
  redirect to('/')
end

post '/get' do
  session[:got] = :queue_empty
  #Post.messages_queue(params[:host]).subscribe(:ack => true, :timeout => 10,
  #                         :message_max => 1) do |msg|
  #  session[:got] = msg[:payload]
  #end
  cli = Post.client(params[:host2])
  rlt = cli.find_one({"zwzkey" => params[:key2]})      #.to_a.to_s
  session[:got] = rlt!=nil ?  rlt['zwzval'] : nil
  cli.remove
#  p "result #{rlt}"
  redirect to('/')
  
end
