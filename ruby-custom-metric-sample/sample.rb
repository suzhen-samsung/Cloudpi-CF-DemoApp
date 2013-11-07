require 'rubygems'
require 'sinatra'
require 'json/pure'

require 'cloudpi-appender'

policy = {}
policy[:custom1] = { :type => "number", :collect => "sum" }

a = CloudPI::Appender.new
a.set_policy(policy)

get '/' do
  res = "<html><body style=\"margin:0px auto; width:80%; font-family:monospace\">" 
  res << "<head><title>CloudFoundry Environment</title></head>"
  res << "<h3>CloudFoundry Environment - Appender</h3>"
  res << "<div><table>"
  ENV.keys.sort.each do |key|
    value = begin
              "<pre>" + JSON.pretty_generate(JSON.parse(ENV[key])) + "</pre>"
            rescue
              ENV[key]
            end
    res << "<tr><td><strong>#{key}</strong></td><td>#{value}</tr>"
  end
  res << "</table></div></body></html>"
end

# custom metric monitoring sample
list = []
get '/sum/:number' do |number|
  metric_value = {:custom1 => number.to_f}
  a.send(metric_value)
  puts "#{Time.now.to_i} - #{metric_value.to_json}"

  list << number.to_f

  res = "<html><body style=\"margin:0px auto; width:80%; font-family:monospace\">" 
  res << "<head><title>#{number}</title></head>"
  res << "<body><h1>list: #{list.to_json}</h1><h1>sum: #{list.inject(:+)}</h1>"
  res << "</body></html>"
end