require 'sinatra'
require 'erb'

require 'bunny'
require 'json'

require 'cpruntime/rabbitmqclient'
require 'yaml'

enable :sessions

class Hosts
  attr_accessor :hostname
end

class Post
  
  include CPRuntime
  def self.index
    @hosts = [];
    m =   credential_map('rabbit')
    i=0
    m.each{|k,v|
      h = Hosts.new
      h.hostname = k
      @hosts[i] = h
      i = i+1
    }
    # p @hosts
  end

def self.amqp_url
  services = JSON.parse(ENV['VCAP_SERVICES'], :symbolize_names => true)
  srv=services.values[0][0][:credentials]
    srv.each {|s|
      #return "amqp://"+s[:username]+":"+s[:password]+"@"+s[:host].to_s()+":"+s[:port].to_s+"/"+s[:vhost]
      return {:host => s[:hostname], :port => s[:port], :pass => s[:password], :user=>s[:user],:vhost=>s[:vhost]}
    }
end

def self.client(hostname)
  if $use_cloudpi_source
      c = CPRuntime::RabbitMQClient.new.create_from_svc(hostname)
      c.start
      @client = c
  else
    unless $client
      c = Bunny.new(amqp_url)
      c.start
      @client = c
      @client.qos :prefetch_count => 1
    end 
  end
  @client
end

def self.nameless_exchange(hostname)
  @nameless_exchange ||= client(hostname).exchange('')
end

def self.messages_queue(hostname)
  @messages_queue ||= client(hostname).queue("messages")
end

end

def take_session key
  res = session[key]
  session[key] = nil
  res
end
  include CPRuntime
get '/' do
  @published = take_session(:published)
  @got = take_session(:got)
  @env = ENV['VCAP_SERVICES']
    @hosts = [];
    m =   credential_map('rabbit')
    i=0
    m.each{|k,v|
      h = Hosts.new
      h.hostname = k
      @hosts[i] = h
      i = i+1
      p h.hostname
    }
    p @hosts
  erb :index
end

post '/publish' do
  Post.nameless_exchange(params[:host]).publish params[:message], :content_type => "text/plain",
                            :key => "messages"
  session[:published] = true
  redirect to('/')
end

post '/get' do
  session[:got] = :queue_empty
  Post.messages_queue(params[:host]).subscribe(:ack => true, :timeout => 10,
                           :message_max => 1) do |msg|
    session[:got] = msg[:payload]
  end
  redirect to('/')
  
end
